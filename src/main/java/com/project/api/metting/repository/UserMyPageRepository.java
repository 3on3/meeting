package com.project.api.metting.repository;

import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMyPageRepository extends JpaRepository<User, String> {

    Optional<User> findById(String id);

}