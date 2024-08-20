package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
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
import java.time.*;
import java.util.*;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMyPageService {

    @Value("${study.mail.host}")
    private String mailHost;
    private String profileImageUploadDir; // 프로필 이미지를 저장할 디렉토리 경로
    private final UserMyPageRepository userMyPageRepository;
    private final UserProfileRepository userProfileRepository; // UserProfile 엔티티를 다루는 JPA 리포지토리
    private final UserRepository userRepository; // User 엔티티를 다루는 JPA 리포지토리
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationRepository userVerificationRepository;
    // 이메일 전송 객체
    private final JavaMailSender mailSender;

    private final AwsS3Service s3Service;


    /**
     * 파일 업로드 처리
     *
     * @param profileImage - 클라이언트가 전송한 파일 바이너리 객체
     * @param userId       - 사용자 ID
     * @return - 업로드된 파일의 URL
     */
    public String uploadProfileImage(MultipartFile profileImage, String userId) throws IOException {

        // 파일명을 유니크하게 변경
        String uniqueFileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();

        // 파일을 S3 버킷에 저장
        String url = s3Service.uploadToS3Bucket(profileImage.getBytes(), uniqueFileName);
        log.info("Uploaded file URL: {}", url);

        // 사용자 찾기
        User findUser = userRepository.findById(userId).orElseThrow(() -> new IOException("User not found"));

        // UserProfile 찾기
        UserProfile userProfile = userProfileRepository.findByUserId(findUser.getId());

        if (userProfile == null) {
            // UserProfile이 없으면 새로 생성
            userProfile = UserProfile.builder()
                                    .user(findUser)
                                    .profileImg(url)  // 초기 프로필 이미지 설정
                                    .build();
            log.info("Created new UserProfile: {}", userProfile);
        } else {
            // UserProfile이 존재하면 업데이트
            userProfile.setProfileImg(url);
            log.info("Updated UserProfile: {}", userProfile);
        }

        // UserProfile 저장
        userProfileRepository.save(userProfile);

        return url;
    }

    //================================================

    // 유저 정보 가져오기
    public UserMyPageDto getUserInfo(String userId) {
        User user = userMyPageRepository.findById(userId)
                                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        log.info("User information retrieved successfully for email: {}", userId);
        return convertToDto(user);
    }

    private UserMyPageDto convertToDto(User user) {
        log.info("Converting user entity to DTO for user: {}", user.getNickname());

        System.out.println(user.getUserProfile());

         return UserMyPageDto.builder()
                .profileIntroduce(user.getUserProfile() != null && user.getUserProfile().getProfileIntroduce() != null
                        ? user.getUserProfile().getProfileIntroduce()
                        : "소개가 없습니다.")
                 .profileImg(user.getUserProfile().getProfileImg())
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

    // 유저 정보 업데이트
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
        if (updateDto.getProfileIntroduce() != null) {
            UserProfile userProfile = user.getUserProfile();

            if (userProfile == null) {
                // UserProfile 객체가 없는 경우 새로 생성
                userProfile = new UserProfile();
            }

            // profileIntroduce 값을 설정
            userProfile.setProfileIntroduce(updateDto.getProfileIntroduce());

            // User 객체에 UserProfile 설정
            user.setUserProfile(userProfile);
        }

        userMyPageRepository.save(user);
        return convertToUserMyPageDto(user);
    }

    // - 프로필 이미지 조회
    public UserProfile getUserProfile(String userId) {
        // 유저 ID로 User 객체를 조회
        Optional<User> user = userRepository.findById(userId);
        System.out.println("user================================ : " + user);

        // 조회한 User 객체와 연결된 UserProfile을 반환
        return user.map(userProfileRepository::findByUser).orElse(null);
    }

    // - 프로필 이미지를 업데이트
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

        if (changePasswordDto.getNewPassword() == null ||
                changePasswordDto.getConfirmNewPassword() == null ||
                !changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않거나 null입니다");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userMyPageRepository.save(user);
    }

//    private boolean notFinish(String email) {
//        User findUser = userRepository.findByEmail(email).orElseThrow();
//
//        UserVerification ev = userVerificationRepository
//                .findByUser(findUser)
//                .orElse(null);
//
//        if (ev != null) userVerificationRepository.delete(ev);
//
//            // 인증코드 재발송
//            generateAndCreateCode(email, findUser);
//            return true;
//    }

    @Transactional
    private void generateAndSaveCode(String email, User user, String code) {
        log.info("email - info - {}", email);

        // 인증 코드 정보를 데이터베이스에 저장
        UserVerification temporaryVerification = UserVerification.builder()
                .user(user)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .verificationCode(code)
                .build();

        userVerificationRepository.save(temporaryVerification);
    }


    // 이메일 인증 코드 보내기
    // 이메일 인증 코드 보내기
    @Transactional
    public String sendVerificationEmail(String email) {
        log.info("email send info - {}", email);

        User findUser = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 검증 코드 생성하기
        String code = generateVerificationCode();

        // 이메일을 전송할 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구에게 이메일을 보낼 것인지
            messageHelper.setTo(email);
            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일] 과팅 가입 인증 메일입니다.");
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

            // 이메일 전송 후 인증 코드를 저장
            generateAndSaveCode(email, findUser, code);

            return code;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 검증 코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }

    // 인증코드 체크 : 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치하는 경우
    public boolean isMatchCode(String email, String code) {

        // 이메일을 통해 회원정보를 탐색
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user != null) {
            // 인증코드가 있는지 탐색
            UserVerification ev = userVerificationRepository.findByUser(user)
                    .orElse(null);
            // 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && code.equals(ev.getVerificationCode())
            ) {
                return true;
            }
        }
        return false;
    }


    @Transactional // 인증코드 확인 버튼 클릭 시 보내는 거
    public boolean verifySendingCode(TemporaryVerficationDto verificationDto) {
        // 이메일을 통해 회원정보를 탐색
        User findUser = userRepository.findByEmail(verificationDto.getEmail()).orElse(null);
        if (findUser != null) {
            // 인증코드가 있는지 탐색
            UserVerification ev = userVerificationRepository.findByUser(findUser).orElse(null);
            // 인증코드가 있고, 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && verificationDto.getCode().equals(ev.getVerificationCode())) {
                // 인증코드 데이터베이스에서 삭제
                userVerificationRepository.delete(ev);
                return true;
            } else {
                // 인증코드가 틀렸거나 만료된 경우
                // 기존 인증코드 삭제
                if (ev != null) {
                    userVerificationRepository.delete(ev);
                }
                // 인증 실패에 대한 명확한 메시지 제공
                throw new IllegalArgumentException("인증 코드가 틀리거나 만료되었습니다. 새로운 인증 코드가 이메일로 전송되었습니다.");
            }
        }
        return false;
    }

    private void generateAndCreateCode(String email, User findUser) {
        //2. 이메일 인증코드 발송
        String code = sendVerificationEmail(email);

        //3. 인증 코드 정보를 데이터베이스에 저장
        UserVerification verification = UserVerification.builder()
                .verificationCode(code) //인증코드
                .expiryDate(LocalDateTime.now().plusMinutes(5)) //만료 시간 (5분뒤)
                .user(findUser) // FK
                .build();

        userVerificationRepository.save(verification);
    }

    public boolean verifyPassword(PasswordVerificationDto verificationDto) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 입력된 비밀번호와 저장된 비밀번호 해시값 비교
        if (passwordEncoder.matches(verificationDto.getPassword(), user.getPassword())) {
            return true;
        }
        return false;
    }

    // 비밀번호 확인 메서드
    public boolean checkPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 입력한 비밀번호와 DB의 암호화된 비밀번호를 비교
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    public void updatePhoneNumber(String email, UpdatePhoneNumberDto dto) {
        if (dto == null || dto.getPhoneNumber() == null) {
            throw new IllegalArgumentException("전화번호 데이터는 null일 수 없습니다");
        }
        boolean phoneExists = userMyPageRepository.existsByPhoneNumber(dto.getPhoneNumber());
        if (phoneExists) {
            throw new IllegalArgumentException("이미 존재하는 번호입니다.");
        }

        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email로 사용자를 찾을 수 없습니다: " + email));

        user.setPhoneNumber(dto.getPhoneNumber());
        userMyPageRepository.save(user);
    }

    public void withDrawnUser(String email, TokenUserInfo tokenUserInfo) {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        findUser.setIsWithdrawn(true);
        userRepository.save(findUser);
    }
}