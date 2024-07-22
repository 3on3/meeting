package com.project.api.metting.repository;

import com.project.api.metting.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {
}
