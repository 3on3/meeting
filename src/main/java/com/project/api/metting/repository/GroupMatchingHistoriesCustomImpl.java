package com.project.api.metting.repository;

import com.project.api.metting.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupMatchingHistoriesCustomImpl implements GroupMatchingHistoriesCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<GroupMatchingHistory> findByResponseGroupId(String groupId) {

        QGroupMatchingHistory qGroupMatchingHistory = QGroupMatchingHistory.groupMatchingHistory;

        List<GroupMatchingHistory> groupMatchingHistoryList = factory.selectFrom(qGroupMatchingHistory)
                .where(qGroupMatchingHistory.responseGroup.id.eq(groupId))
                .fetch();

        return groupMatchingHistoryList;
    }
}
