package com.project.api.metting.service;


import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserVerification;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UserSignUpService {
    @Value("${study.mail.host}")
    private String mailHost;
    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;


    //토큰 생성 객체
    private final TokenProvider tokenProvider;

    private final JavaMailSender mailSender;
    // 패스워드 암호화 객체
//    private final PasswordEncoder encoder;
    private final PasswordEncoder encoder;

    // 이메일 중복확인 처리
    public boolean checkEmailDuplicate(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate : {}", email, exists);
        if (exists && notFinish(email)) {
            return false;
        }
        if (!exists) processSignUp(email);

        return exists;
    }


    private boolean notFinish(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!user.getIsVerification() || user.getPassword() == null) {
            // 기존 인증코드가 있는 경우 삭제
            UserVerification ev = userVerificationRepository
                    .findByUser(user)
                    .orElse(null);

            if (ev != null) userVerificationRepository.delete(ev);

            // 인증코드 재발송
            generateAndCreateCode(email, user);
            return true;
        }
        return false;
    }

    public void processSignUp(String email) {

        // 1. 임시 회원가입
        User savedUser = User
                .builder()
                .email(email)
                .build();


        User save = userRepository.save(savedUser);
        generateAndCreateCode(email, save);

    }

    private void generateAndCreateCode(String email, User user) {
        //2. 이메일 인증코드 발송
        String code = sendVerificationEmail(email);

        //3. 인증 코드 정보를 데이터베이스에 저장
        UserVerification verification = UserVerification.builder()
                .verificationCode(code) //인증코드
                .expiryDate(LocalDateTime.now().plusMinutes(5)) //만료 시간 (5분뒤)
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
            messageHelper.setText(
                    "인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>"
                    , true
            );

            // 전송자의 이메일 주소
            messageHelper.setFrom(mailHost);

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


    //인증코드 체크
    public boolean isMatchCode(String email, String code) {
        //이메일을 통해 회원정보를 탐색
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            //인증코드가 있는지 탐색
//            EmailVerification ev = emailVerificationRepository.findByEventUser(eventUser).orElse(null);
            UserVerification ev = userVerificationRepository.findByUser(user).orElse(null);
            //인증코드가 있고, 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev!= null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && code.equals(ev.getVerificationCode())
            ) {

                //이메일 ㅣㅇㄴ증여부 true로 수정
                user.setIsVerification(true);
                userRepository.save(user);

                //인증코드 데이터베이스에서 삭제
                userVerificationRepository.delete(ev);
                return true;
            } else {//인증코드가 틀렸거나 만료된 경우
                //인증코드 재발송
                //원래 인증코드 삭제
                userVerificationRepository.delete(ev);

                //새인증코드 발급 이메일 재전송
                //데이터베이스에 새 인증코드 저장
                generateAndCreateCode(email, user);
                return false;
            }
        }



        return false;
    }


    //회원가입 마무리
    public void confirmSignUp(UserRegisterDto dto) {
        // 기존 회원정보 조회
        User findUser = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new LoginFailException("회원 정보가 존재하지 않습니다.")
        );

        log.info("Confirm sign up : {}", dto);

        //데이터 반영 (패스웓, 가입시간)
        String password = dto.getPassword();
        findUser.confirm(password, dto.getName(), dto.getBirthDate(), dto.getPhoneNumber(), dto.getUniv(),
                dto.getMajor(), dto.getGender(), dto.getNickname());
        String encodedPassword = encoder.encode(password); // 암호화

        findUser.changePass(encodedPassword);
        userRepository.save(findUser);


    }

}
