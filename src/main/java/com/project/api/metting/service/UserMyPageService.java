package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.UpdatePhoneNumberDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.request.RemoveUserDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.repository.UserMyPageRepository;
import com.project.api.metting.repository.UserProfileRepository;
import com.project.api.metting.repository.UserRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMyPageService {

    @Value("${univcert.api.key}")
    private String univCertApiKey;

    private String profileImageUploadDir; // 프로필 이미지를 저장할 디렉토리 경로

    private final UserMyPageRepository userMyPageRepository;
    private final UserProfileRepository userProfileRepository; // UserProfile 엔티티를 다루는 JPA 리포지토리
    private final UserRepository userRepository; // User 엔티티를 다루는 JPA 리포지토리
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


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
    public UserProfile getUserProfile (String userId){
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

        if (changePasswordDto.getNewPassword() == null ||
                changePasswordDto.getConfirmNewPassword() == null ||
                !changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않거나 null입니다");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userMyPageRepository.save(user);
    }


/**
 * 회원 탈퇴를 위한 인증 코드를 사용자 이메일로 전송하는 메서드.
 * 인증 상태나 이전 요청과 관계없이 새로운 인증 코드를 생성하여 전송
 *
 * @param email 사용자 이메일
 */
    public void sendRemovalVerificationCode(String email) {
        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 유저가 없습니다."));

        String code = String.format("%06d", (int) (Math.random() * 1000000)); // 새 인증 코드 생성
        saveVerificationCode(email, code); // 새 인증 코드 저장

        try {
            // UnivCert API를 통해 인증 코드 발송 요청
            Map<String, Object> response = UnivCert.certify(univCertApiKey, email, user.getUnivName(), false);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("새로운 인증 코드가 이메일로 전송되었습니다: {}", email);
            } else if ("이미 완료된 요청입니다.".equals(response.get("message"))) {
                log.warn("이미 완료된 요청: {}", email);
                // 기존 요청이 완료되었어도 새 인증 코드를 전송하기 위한 이메일 전송 로직
                emailService.sendEmail(email, "인증 코드", "귀하의 인증 코드는 " + code + "입니다.");
                log.info("기존 요청이 완료되었으므로 새로운 인증 코드를 이메일로 전송했습니다: {}", email);
            } else {
                log.error("인증 코드 발송 실패: {}", email);
                throw new IllegalStateException("인증 코드 발송에 실패했습니다.");
            }
        } catch (IOException e) {
            log.error("인증 코드 발송 중 예외 발생: ", e);
            throw new IllegalStateException("인증 코드 발송 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("예상치 못한 예외 발생: ", e);
            throw new IllegalStateException("인증 코드 발송 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }

    private void saveVerificationCode(String email, String code) {

    }


    /**
     * 인증 코드와 비밀번호를 확인한 후 사용자를 탈퇴 처리하는 메서드
     *
     * @param user           사용자 이메일
     * @param inputPassword    사용자 입력 비밀번호
     *

     */

    private void verifyUserPassword(User user, String inputPassword) {
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다: {}", user.getEmail());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void removeUserWithVerification(String email, int verificationCode, String inputPassword) {
        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 유저가 없습니다."));

        try {
            Map<String, Object> response = UnivCert.certifyCode(univCertApiKey, email, user.getUnivName(), verificationCode);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("인증 코드가 확인되었습니다: {}", email);

                verifyUserPassword(user, inputPassword);

                user.setEmail(null);
                user.setPassword(null);
                user.setIsWithdrawn(true);

                userMyPageRepository.save(user);
                log.info("회원 탈퇴 처리 완료: {}", email);
            } else {
                log.error("인증 코드가 유효하지 않습니다: {}", email);
                throw new IllegalArgumentException("인증 코드가 유효하지 않습니다.");
            }
        } catch (IOException e) {
            log.error("인증 코드 검증 중 예외 발생: ", e);
            throw new IllegalStateException("인증 코드 검증 중 오류가 발생했습니다.");
        }
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
}