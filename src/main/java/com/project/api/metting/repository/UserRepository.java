package com.project.api.metting.repository;

import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByEmailAndRefreshToken(String email, String refreshToken);
    void deleteByEmail(String email);
}
