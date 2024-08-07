package com.project.api.metting.service;

import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserVerification;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.repository.UserVerificationRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UserSignUpService {

    @Value("${univcert.api.key}")
    private String univCertApiKey;

    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;

    private final JavaMailSender mailSender;
    private final PasswordEncoder encoder;

    // 이메일 중복확인 처리
    public boolean checkEmailDuplicate(String email, String univName) {
        boolean exists = userRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate: {}", email, exists);
        return exists; // 중복 여부만 반환
    }

    private boolean notFinish(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (!user.getIsVerification() || user.getPassword() == null) {
            // 기존 인증코드가 있는 경우 삭제
            UserVerification ev = userVerificationRepository.findByUser(user).orElse(null);
            if (ev != null) userVerificationRepository.delete(ev);
            // 인증코드 재발송
            generateAndCreateCode(email, user);
            return true;
        }
        return false;
    }

    public void processSignUp(String email, String univName) {
        try {
            // Step 1: API 호출을 통해 인증 메일 발송
            Map<String, Object> response = UnivCert.certify(univCertApiKey, email, univName, false);

            // Step 2: 인증 메일 발송 성공 시 임시 회원가입
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                User savedUser = User.builder()
                        .email(email)
                        .univName(univName)
                        .build();
                User saved = userRepository.save(savedUser);
            } else {
                log.error("Failed to send verification email to {}", email);
            }
        } catch (IOException e) {
            log.error("Exception while sending verification request: ", e);
        }
    }

    private void generateAndCreateCode(String email, User user) {
        // 2. 이메일 인증코드 발송
        String code = sendVerificationEmail(email);

        // 3. 인증 코드 정보를 데이터베이스에 저장
        UserVerification verification = UserVerification.builder()
                .verificationCode(code) // 인증코드
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // 만료 시간 (5분 뒤)
                .user(user) // FK
                .build();
        userVerificationRepository.save(verification);
    }

    // 이메일 인증 코드 보내기
    public String sendVerificationEmail(String email) {
        // 검증 코드 생성하기
        String code = generateVerificationCode();
        // 이메일을 전송할 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 누구에게 이메일을 보낼 것인지
            messageHelper.setTo(email);
            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일]과팅 어플 가입 인증 메일입니다.");
            // 이메일 내용 설정
            messageHelper.setText("인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>", true);
            // 이메일 보내기
            mailSender.send(mimeMessage);
            log.info("{} 님에게 이메일 전송!", email);
            return code;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 검증 코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }

    // 인증코드 체크
    public boolean isMatchCode(String email, String code) {
        // 이메일을 통해 회원정보를 탐색
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            // 인증코드가 있는지 탐색
            UserVerification ev = userVerificationRepository.findByUser(user).orElse(null);
            // 인증코드가 있고, 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null && ev.getExpiryDate().isAfter(LocalDateTime.now()) && code.equals(ev.getVerificationCode())) {
                // 이메일 인증 여부 true로 수정
                user.setIsVerification(true);
                userRepository.save(user);
                // 인증코드 데이터베이스에서 삭제
                userVerificationRepository.delete(ev);
                return true;
            } else {
                // 인증코드가 틀렸거나 만료된 경우
                // 인증코드 재발송, 원래 인증코드 삭제
                userVerificationRepository.delete(ev);
                // 새 인증코드 발급, 이메일 재전송, 데이터베이스에 새 인증코드 저장
                generateAndCreateCode(email, user);
                return false;
            }
        }
        return false;
    }

    public boolean verifyCode(String email, int code) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                log.error("User not found for email: {}", email);
                return false;
            }

            String univName = user.getUnivName(); // univName 가져오기
            if (univName == null) {
                log.error("University name not found for user: {}", email);
                return false;
            }

            Map<String, Object> response = UnivCert.certifyCode(univCertApiKey, email, univName, code);
            return response != null && Boolean.TRUE.equals(response.get("success"));
        } catch (IOException e) {
            log.error("Exception while verifying code: ", e);
            return false;
        }
    }


    // 회원가입 마무리
    @Transactional
    public void confirmSignUp(UserRegisterDto dto) {
        User findUser = userRepository.findByEmail(dto.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(dto.getEmail());
            return newUser;
        });
        log.info("Confirm sign up : {}", dto);
        String password = dto.getPassword();
        findUser.confirm(password, dto.getName(), dto.getBirthDate(), dto.getPhoneNumber(), dto.getUnivName(), dto.getMajor(), dto.getGender(), dto.getNickname());
        String encodedPassword = encoder.encode(password);
        findUser.changePass(encodedPassword);
        userRepository.save(findUser);
    }
}
