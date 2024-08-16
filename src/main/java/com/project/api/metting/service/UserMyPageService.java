package com.project.api.metting.service;

import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TemporaryVerificationRepository temporaryVerificationRepository;

    // 이메일 전송 객체
    private  final JavaMailSender mailSender;

    // 유저 정보 가져오기
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

        userMyPageRepository.save(user);
        return convertToUserMyPageDto(user);
    }

    // - 프로필 이미지 조회
    public UserProfile getUserProfile(String userId) {
        // 유저 ID로 User 객체를 조회
        Optional<User> user = userRepository.findById(userId);
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


    // - 비밀번호 변경 로직
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


    // - 이메일 중복 확인
    public boolean checkEmailDuplicate(String email) {

        boolean exists = userRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate : {}", email, exists);
        return userRepository.existsByEmail(email);// 이메일이 존재하면 true 리턴
    }


    // - 이메일 인증 코드 보내기
    @Transactional
    public void sendVerificationEmail(String email) {
        // 검증 코드 생성하기
        String code = generateVerificationCode();
        TemporaryVerification temporaryVerification = TemporaryVerification.builder()
                                                        .email(email)
                                                        .code(code)
                                                        .build();

        temporaryVerificationRepository.save(temporaryVerification);

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

    public boolean verifySendingCode(TemporaryVerficationDto verficationDto) {
        TemporaryVerification verification = temporaryVerificationRepository
                                            .findByEmail(verficationDto.getEmail());
        if(verification.getCode().equals(verficationDto.getCode())) {
            return true;
        }
        return false;
    }

    public boolean verifyPassword(PasswordVerificationDto verificationDto) {
        User user = userRepository.findByEmail(verificationDto.getEmail()).orElseThrow();
        if(user.getPassword().equals(verificationDto.getPassword())) {
            return true;
        }
        return false;
    }
}