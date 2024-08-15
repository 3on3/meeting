package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.entity.TemporaryVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryVerificationRepository extends JpaRepository<TemporaryVerification, String> {

    TemporaryVerification findByEmail(String email);
}
