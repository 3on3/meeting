package com.project.api.metting.service;

import com.project.api.metting.entity.UserVerification;
import com.project.api.metting.repository.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserVerificationService {

    @Autowired
    private UserVerificationRepository userVerificationRepository;

    public void saveVerificationCode(String email, String code) {
        // 기존에 존재하는 인증 코드를 삭제 또는 업데이트
        UserVerification existingVerification = userVerificationRepository.findByEmail(email);

        if (existingVerification != null) {
            // 기존 레코드를 업데이트
            existingVerification.setVerificationCode(code);
            existingVerification.setExpiryDate(LocalDateTime.now().plusMinutes(3)); // 3분 후 만료
            userVerificationRepository.save(existingVerification);
        } else {
            // 새로운 레코드를 생성
            UserVerification verificationCode = new UserVerification();
            verificationCode.setEmail(email);
            verificationCode.setVerificationCode(code);
            verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(3)); // 3분 후 만료

            userVerificationRepository.save(verificationCode);
        }
    }

    public String getVerificationCode(String email) {
        UserVerification verificationCode = userVerificationRepository.findByEmail(email);
        if (verificationCode != null && verificationCode.getExpiryDate().isAfter(LocalDateTime.now())) {
            return verificationCode.getVerificationCode();
        }
        return null; // 만료되었거나 없는 경우
    }
}
