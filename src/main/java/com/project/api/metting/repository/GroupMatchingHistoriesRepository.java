package com.project.api.metting.repository;

import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface GroupMatchingHistoriesRepository extends JpaRepository<GroupMatchingHistory, String> {



    boolean existsByResponseGroupAndRequestGroup(Group responseGroup, Group requestGroup);
    GroupMatchingHistory findByResponseGroupAndRequestGroup(Group responseGroup, Group requestGroup);

    GroupMatchingHistory findByResponseGroupAndProcess(Group group, GroupProcess process);

    GroupMatchingHistory findByRequestGroupAndProcess(Group group, GroupProcess process);
}
