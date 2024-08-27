package com.project.api.metting.service;

import com.project.api.metting.dto.request.NewPasswordDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordFindService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 존재 여부 확인
    public boolean checkUserExistsByEmail(String email) {
        boolean userExists = userRepository.existsByEmail(email);
        log.info("Checking existence of email: {}, userExists: {}", email, userExists);
        return userExists;
    }

    // 새로운 비밀번호 설정
    public void resetPassword(String email, NewPasswordDto dto) {
        // 비밀번호 일치 여부 확인
        if (dto.getNewPassword() == null || dto.getConfirmNewPassword() == null || !dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않거나 null입니다.");
        }

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 사용자를 찾을 수 없습니다."));

        // 비밀번호 인코딩 후 저장
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
