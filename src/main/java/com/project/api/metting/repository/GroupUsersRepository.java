package com.project.api.metting.repository;

import com.project.api.metting.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUsersRepository extends JpaRepository<GroupUser, String> {
}
