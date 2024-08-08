package com.project.api.metting.repository;

import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String>, UserProfileRepositoryCustom {
}
