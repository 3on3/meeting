package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.request.RemoveUserDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserMyPageRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMyPageService {


    private final UserMyPageRepository userMyPageRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${univcert.api.key}")
    private String univCertApiKey;


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
                .profileImg(user.getUserProfile() != null ? user.getUserProfile().getProfileImg() : null)
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
        log.info("User fields updated and saved for user ID: {}", userId);

        return convertToUserMyPageDto(user);
    }

    // User 엔티티를 UserMyPageDto로 변환하는 로직
    public UserMyPageDto convertToUserMyPageDto(User user) {
        log.info("Converting updated user entity to DTO for user: {}", user.getNickname());
        UserMyPageDto dto = new UserMyPageDto();

        // 프로필 이미지와 소개가 있는 경우 설정
        if (user.getUserProfile() != null) {
            dto.setProfileImg(user.getUserProfile().getProfileImg());
            dto.setProfileIntroduce(user.getUserProfile().getProfileIntroduce());
        }

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


    /**
     * 회원 탈퇴를 위한 인증 코드를 사용자 이메일로 전송하는 메서드.
     * 인증 상태나 이전 요청과 관계없이 새로운 인증 코드를 생성하여 전송합니다.
     *
     * @param email 사용자 이메일
     */
    public void sendRemovalVerificationCode(String email) {
        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 유저가 없습니다."));

        try {
            Map<String, Object> response = UnivCert.certify(univCertApiKey, email, user.getUnivName(), false);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("새로운 인증 코드가 이메일로 전송되었습니다: {}", email);
            } else if ("이미 완료된 요청입니다.".equals(response.get("message"))) {
                log.warn("이미 완료된 요청: {}", email);
                // 필요시 추가 로직 구현 (예: 일정 시간 후 재시도)
            } else {
                log.error("인증 코드 발송 실패: {}", email);
                throw new IllegalStateException("인증 코드 발송에 실패했습니다.");
            }
        } catch (IOException e) {
            log.error("인증 코드 발송 중 예외 발생: ", e);
            throw new IllegalStateException("인증 코드 발송 중 오류가 발생했습니다.");
        }
    }



    /**
     * 인증 코드와 비밀번호를 확인한 후 사용자를 탈퇴 처리하는 메서드
     *
     * @param email           사용자 이메일
     * @param verificationCode 사용자 인증 코드
     * @param inputPassword   사용자 입력 비밀번호
     */
    @Transactional
    public void removeUserWithVerification(String email, int verificationCode, String inputPassword) {
        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 유저가 없습니다."));

        try {
            Map<String, Object> response = UnivCert.certifyCode(univCertApiKey, email, user.getUnivName(), verificationCode);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                log.info("인증 코드가 확인되었습니다: {}", email);

                if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
                    log.error("비밀번호가 일치하지 않습니다: {}", email);
                    throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }

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
}
