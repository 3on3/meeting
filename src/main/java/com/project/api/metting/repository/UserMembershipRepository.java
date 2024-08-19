package com.project.api.metting.repository;

import com.project.api.metting.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMembershipRepository extends JpaRepository<UserMembership, String>,UserMembershipRepositoryCustom {
}
