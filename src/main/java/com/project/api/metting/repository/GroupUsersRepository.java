package com.project.api.metting.repository;

import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupStatus;
import com.project.api.metting.entity.GroupUser;
import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUsersRepository extends JpaRepository<GroupUser, String> {
    boolean existsByUserAndGroupAndStatus(User user, Group group, GroupStatus status);
    boolean existsByUserAndGroup(User user, Group group);
}
