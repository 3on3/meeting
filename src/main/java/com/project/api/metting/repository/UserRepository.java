package com.project.api.metting.repository;

import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
