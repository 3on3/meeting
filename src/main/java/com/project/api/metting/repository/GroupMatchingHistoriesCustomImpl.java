package com.project.api.metting.repository;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.QGroup;
import com.project.api.metting.entity.QGroupMatchingHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupMatchingHistoriesCustomImpl implements GroupMatchingHistoriesCustom {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<ChatRequestDto> findGroupById(String groupId) {

        QGroup group = QGroup.group;
//        jpaQueryFactory.select(GroupMatchingHistory).where()


        return List.of();
    }
}
