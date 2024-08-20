package com.project.api.metting.repository;

import com.project.api.metting.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface GroupUsersRepository extends JpaRepository<GroupUser, String> {
    boolean existsByUserAndGroupAndStatus(User user, Group group, GroupStatus status);
    boolean existsByUserAndGroup(User user, Group group);
    List<GroupUser> findByGroupAndStatus(Group group, GroupStatus status);
    GroupUser findByGroupAndAuth(Group group, GroupAuth auth);
    List<GroupUser> findByGroup(Group group);
    Optional<GroupUser> findByGroupAndUserIdAndStatus(Group group, String userId, GroupStatus status);
    List<GroupUser> findByUserAndStatus(User user, GroupStatus groupStatus);

    long countByGroupAndStatus(Group group, GroupStatus groupStatus);
}
