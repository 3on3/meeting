package com.project.api.metting.repository;

import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {

    Optional<UserVerification> findByUser(User user);
}
