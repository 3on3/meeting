package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.repository.UserMyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;


    @Service
    @RequiredArgsConstructor
    public class UserMyPageService {


        private final UserMyPageRepository userMyPageRepository;

        private PasswordEncoder passwordEncoder;

        /**
         * 사용자 ID를 기반으로 유저 정보 조회
         *
         * @param userId 조회할 사용자의 ID
         * @return UserProfileDto 객체를 담은 Optional
         */

        public Optional<UserMyPageDto> getUserInfo(String userId) {
            Optional<User> user = userMyPageRepository.findById(userId);

            // user가 존재한다면
            if (user.isPresent()) {
                // user.get()을 사용하여 User 객체를 가져오고,
                // convertToDto(userOpt.get())를 통해 User 객체를 UserMyPageDto로 변환한 후
                // Optional.of()를 사용하여 UserMyPageDto를 Optional로 감싸서 반환
                return Optional.of(convertToDto(user.get()));
            } else {
                // user가 존재하지 않으면 빈 객체 반환
                return Optional.empty();
            }
        }

        private UserMyPageDto convertToDto(User user) {
            UserProfile profile = user.getUserProfile();
            return UserMyPageDto.builder()
                    .password(user.getPassword())
                    .birthDate(user.getBirthDate())
                    .age(calculateAge(user.getBirthDate()))
                    .phoneNumber(user.getPhoneNumber())
                    .major(user.getMajor())
                    .nickname(user.getNickname())
                    .membership(user.getMembership())
                    .profileImg(profile != null ? profile.getProfileImg() : null)
                    .profileIntroduce(profile != null ? profile.getProfileIntroduce() : null)
                    .build();
        }

        private int calculateAge(Date birthDate) {
            if (birthDate == null) {
                return 0;
            }
            LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthLocalDate, LocalDate.now()).getYears();
        }

        public boolean changePassword(String userId, ChangePasswordDto changePasswordDto ) {
            Optional<User> userOpt = userMyPageRepository.findById(userId);
           if (userOpt.isPresent()) {
                User user = userOpt.get();

            // 현재 비밀번호가 일치하는지 확인
            if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
                return false; // 현재 비밀번호가 일치하지 않음
            }

            // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
                return false; // 새 비밀번호와 확인 비밀번호가 일치하지 않음
            }

            // 새로운 비밀번호로 설정
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userMyPageRepository.save(user); // 변경된 사용자 정보 저장
            return true; // 비밀번호 변경 성공
        }
        return false; // 유저가 존재하지 않음
    }
}

