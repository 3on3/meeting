package com.project.api.metting.repository;

import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;

import java.util.List;

interface GroupMatchingHistoriesCustom {

    // 주최자 그룹 기준 히스토리 배열
    List<GroupMatchingHistory> findByResponseGroupId(String groupId);
}
