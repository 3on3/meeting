package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.request.RemoveUserDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.entity.UserVerification;
import com.project.api.metting.repository.UserMyPageRepository;
import com.project.api.metting.repository.UserProfileRepository;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.repository.UserVerificationRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMyPageService {

    @Value("${study.mail.host}")
    private String mailHost;

//    @Value("${univcert.api.key}")
//    private String univCertApiKey;

    private String profileImageUploadDir; // 프로필 이미지를 저장할 디렉토리 경로

    private final UserMyPageRepository userMyPageRepository;
    private final UserProfileRepository userProfileRepository; // UserProfile 엔티티를 다루는 JPA 리포지토리
    private final UserRepository userRepository; // User 엔티티를 다루는 JPA 리포지토리

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserVerificationRepository userVerificationRepository;

    // 이메일 전송 객체
    private  final JavaMailSender mailSender;

    // 사용자 정보 가져오기
    public UserMyPageDto getUserInfo(String userEmail) {
        User user = userMyPageRepository.findById(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        log.info("User information retrieved successfully for email: {}", userEmail);
        return convertToDto(user);
    }

    private UserMyPageDto convertToDto(User user) {
        log.info("Converting user entity to DTO for user: {}", user.getNickname());

        return UserMyPageDto.builder()
                .profileIntroduce(user.getUserProfile() != null && user.getUserProfile().getProfileIntroduce() != null
                        ? user.getUserProfile().getProfileIntroduce()
                        : "소개가 없습니다.")
                .nickname(user.getNickname())
                .membership(user.getMembership() != null ? user.getMembership() : Membership.GENERAL)
                .age(calculateAge(user.getBirthDate()))
                .univ(user.getUnivName())
                .major(user.getMajor())
                .build();
    }

    // 나이 계산 메서드
    private int calculateAge(Date birthDate) {
        LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }

    /**
     * 사용자 Id를 기반으로 유저 정보 조회
     * 닉네임, 멤버십등급, 대학교, 전공
     *
     * @param userId 조회할 사용자의 ID
     * @return convertToUserMyPageDto 객체를 담은 Optional
     */

    // 사용자 정보를 업데이트하는 메서드
    public UserMyPageDto updateUserFields(String userId, UserUpdateRequestDto updateDto) {
        log.info("Updating user fields for user ID: {}", userId);
        // 사용자 조회
        User user = userMyPageRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 전달된 DTO의 값들을 사용자 엔티티에 반영
        if (updateDto.getNickname() != null) {
            log.info("Updating nickname to: {}", updateDto.getNickname());
            user.setNickname(updateDto.getNickname());
        }
        if (updateDto.getMembership() != null) {
            Membership membership = Membership.valueOf(updateDto.getMembership().toUpperCase());
            user.setMembership(membership);
        }
        if (updateDto.getUniv() != null) {
            user.setUnivName(updateDto.getUniv());
        }
        if (updateDto.getMajor() != null) {
            user.setMajor(updateDto.getMajor());
        }


        userMyPageRepository.save(user);
        return convertToUserMyPageDto(user);
    }

    // 특정 유저의 프로필을 조회하는 메서드
    public UserProfile getUserProfile(String userId) {
        // 유저 ID로 User 객체를 조회
        Optional<User> user = userRepository.findById(userId);
        // 조회한 User 객체와 연결된 UserProfile을 반환
        return user.map(userProfileRepository::findByUser).orElse(null);
    }

    // 특정 유저의 프로필 이미지를 업데이트하는 메서드
    public void updateUserProfileImage(String userId, MultipartFile file) throws IOException {
        // 유저 ID로 User 객체를 조회
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) { // 유저가 존재하는 경우
            UserProfile userProfile = userProfileRepository.findByUser(user.get()); // 유저와 연결된 프로필을 조회
            if (userProfile != null && file != null && !file.isEmpty()) { // 프로필이 존재하고, 파일이 유효한 경우
                String filePath = saveProfileImage(file);  // 수정: 파일 시스템에 저장 로직 추가
                userProfile.setProfileImg(filePath); // 프로필 이미지 경로를 설정
                userProfileRepository.save(userProfile); // 변경된 프로필을 저장

            }
        }
    }


    // 프로필 이미지를 저장하고, 저장된 파일 경로를 반환하는 메서드
    private String saveProfileImage(MultipartFile file) throws IOException {
        File dir = new File(profileImageUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(profileImageUploadDir, fileName);

        // 예외 처리와 자원 관리를 위한 try-with-resources 사용
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: " + filePath, e);
            throw e; // 발생한 예외를 다시 던져 호출자에게 알림
        }

        return filePath.toString();
    }


    // User 엔티티를 UserMyPageDto로 변환하는 로직
    public UserMyPageDto convertToUserMyPageDto(User user) {
        log.info("Converting updated user entity to DTO for user: {}", user.getNickname());
        UserMyPageDto dto = new UserMyPageDto();

        dto.setNickname(user.getNickname());
        dto.setMembership(user.getMembership());
        dto.setUniv(user.getUnivName());
        dto.setMajor(user.getMajor());

        // 나이 계산
        if (user.getBirthDate() != null) {
            dto.setAge(calculateAge(user.getBirthDate()));
        }

        return dto;
    }


    // 비밀번호 변경 로직
    public void changePassword(String userId, ChangePasswordDto changePasswordDto) {

        if (changePasswordDto == null) {
            throw new IllegalArgumentException("비밀번호 변경 데이터는 null일 수 없습니다");
        }

        User user = userMyPageRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID로 사용자를 찾을 수 없습니다: " + userId));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (changePasswordDto.getNewPassword() == null ||
                changePasswordDto.getConfirmNewPassword() == null ||
                !changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않거나 null입니다");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userMyPageRepository.save(user);
    }



    public boolean checkEmailDuplicate(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate : {}", email, exists);
        return exists;
    }

    // 이메일 인증 코드 보내기
    public void sendVerificationEmail(String email) {

        // 검증 코드 생성하기
        String code = generateVerificationCode();

        // 이메일을 전송할 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구에게 이메일을 보낼 것인지
            messageHelper.setTo(email);
            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일] 회원탈퇴 인증 메일입니다.");
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 검증 코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }
}






//    // 이메일 인증 코드 Redis 저장 시 사용할 키의 접두사입니다.
//    private static final String AUTH_CODE_PREFIX = "AuthCode ";
//
//    // 회원 관련 데이터를 처리하는 리포지토리입니다.
//    private final MemberRepository memberRepository;
//
//    // 이메일 발송을 처리하는 서비스입니다.
//    private final MailService mailService;
//
//    // Redis에 데이터를 저장하고 가져오는 작업을 처리하는 서비스입니다.
//    private final RedisService redisService;
//
//    // 이메일 인증 코드의 유효 기간을 밀리초 단위로 저장합니다. application.properties 또는 application.yml 파일에서 주입받습니다.
//    @Value("${spring.mail.auth-code-expiration-millis}")
//    private long authCodeExpirationMillis;
//
//    // 기타 메서드들이 있을 것으로 예상되어 '...'로 표시하였습니다.
//
//    /**
//     * 주어진 이메일 주소로 인증 코드를 생성하여 발송하는 메서드입니다.
//     * @param toEmail 인증 코드를 발송할 이메일 주소
//     */
//    public void sendCodeToEmail(String toEmail) {
//        // 이메일 중복 여부를 확인합니다. 이미 존재하는 이메일이면 예외가 발생합니다.
//        this.checkDuplicatedEmail(toEmail);
//        // 이메일 제목을 설정합니다.
//        String title = "Travel with me 이메일 인증 번호";
//        // 6자리 인증 코드를 생성합니다.
//        String authCode = this.createCode();
//        // 메일 서비스를 통해 이메일을 발송합니다.
//        mailService.sendEmail(toEmail, title, authCode);
//        // 생성된 인증 코드를 Redis에 저장합니다. 키는 "AuthCode " + 이메일 주소로 설정합니다.
//        redisService.setValues(AUTH_CODE_PREFIX + toEmail,
//                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
//    }
//
//    /**
//     * 주어진 이메일 주소가 이미 회원가입된 상태인지 확인하는 메서드입니다.
//     * @param email 확인할 이메일 주소
//     */
//    private void checkDuplicatedEmail(String email) {
//        // 이메일로 회원을 조회합니다.
//        Optional<User> user = UserRepository.findByEmail(email);
//        // 회원이 존재하면 예외를 발생시킵니다.
//        if (user.isPresent()) {
//            log.debug("MemberServiceImpl.checkDuplicatedEmail exception occur email: {}", email);
//            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
//        }
//    }
//
//    /**
//     * 6자리의 랜덤 인증 코드를 생성하는 메서드입니다.
//     * @return 생성된 인증 코드
//     */
//    private String createCode() {
//        int length = 6; // 생성할 인증 코드의 길이입니다.
//        try {
//            // 보안이 강화된 난수 생성기를 사용합니다.
//            Random random = SecureRandom.getInstanceStrong();
//            StringBuilder builder = new StringBuilder();
//            // 설정된 길이만큼의 숫자를 생성하여 인증 코드를 만듭니다.
//            for (int i = 0; i < length; i++) {
//                builder.append(random.nextInt(10));
//            }
//            return builder.toString(); // 생성된 인증 코드를 반환합니다.
//        } catch (NoSuchAlgorithmException e) {
//            log.debug("MemberService.createCode() exception occur");
//            throw new BusinessLogicException(ExceptionCode.NO_SUCH_ALGORITHM);
//        }
//    }
//
//    /**
//     * 사용자가 입력한 인증 코드를 검증하는 메서드입니다.
//     * @param email    검증할 이메일 주소
//     * @param authCode 사용자가 입력한 인증 코드
//     * @return 인증 결과를 담은 EmailVerificationResult 객체
//     */
//    public EmailVerificationResult verifiedCode(String email, String authCode) {
//        // 이메일 중복 여부를 다시 확인합니다. (이메일이 이미 존재하면 검증이 불필요)
//        this.checkDuplicatedEmail(email);
//        // Redis에서 저장된 인증 코드를 가져옵니다.
//        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
//        // 인증 코드가 존재하고, 사용자가 입력한 코드와 일치하는지 확인합니다.
//        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
//
//        // 인증 결과를 EmailVerificationResult 객체로 반환합니다.
//        return EmailVerificationResult.of(authResult);
//    }


//    /**
//     * 회원 탈퇴를 위한 인증 코드를 사용자 이메일로 전송하는 메서드.
//     */
//
//    public void requestWithdrawal(RemoveUserDto emailRequestDto) {
//        // 1. 입력된 이메일이 가입된 이메일인지 확인
//        String email = emailRequestDto.getEmail();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("이메일이 잘못되었습니다."));
//
//        // 2. 기존 인증 코드가 있으면 삭제 또는 업데이트
//        Optional<UserVerification> existingVerification = Optional.ofNullable(userVerificationRepository.findByEmail(email));
//        existingVerification.ifPresent(userVerificationRepository::delete);
//
//        // 3. 새로운 인증 코드 생성 및 저장
//        String verificationCode = UUID.randomUUID().toString();
//        UserVerification userVerification = UserVerification.builder()
//                .id(UUID.randomUUID().toString())
//                .email(email)
//                .verificationCode(verificationCode)
//                .expiryDate(LocalDateTime.now().plusMinutes(3)) // 인증 코드 유효 시간 설정
//                .user(user)
//                .build();
//
//        userVerificationRepository.save(userVerification);
//
//        // 4. 인증 코드 이메일 발송
//        emailService.sendVerificationEmail(email, verificationCode);
//    }
//
//
//    public void verifyCode(RemoveUserDto verificationCodeDto) {
//        // 4. 입력된 인증 코드와 저장된 코드 확인
//        String email = verificationCodeDto.getEmail();
//        String code = verificationCodeDto.getCode();
//
//        UserVerification userVerification = userVerificationRepository.findByEmail(email);
//
//        if (userVerification != null && userVerification.getVerificationCode().equals(code)
//                && userVerification.getExpiryDate().isAfter(LocalDateTime.now())) {
//        } else {
//            throw new IllegalArgumentException("인증 코드가 잘못되었거나 만료되었습니다.");
//        }
//    }
//
//    public void withdrawUser(RemoveUserDto withdrawRequestDto) {
//        // 5. 비밀번호 확인 및 회원 탈퇴 처리
//        String email = withdrawRequestDto.getEmail();
//        String password = withdrawRequestDto.getPassword();
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("이메일이 잘못되었습니다."));
//
//        if (passwordEncoder.matches(password, user.getPassword())) {
//            userRepository.delete(user);
//        } else {
//            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
//        }
//    }
