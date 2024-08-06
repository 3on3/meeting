package com.project.api.metting.repository;


import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MainFilterRepositoryCustomImpl implements GroupRepositoryCustom{

    private final JPAQueryFactory factory;

    @Override
    public List<GroupResponseDto> findGroupsByUserEmail(String email) {
        return List.of();
    }

    @Override
    public List<MainMeetingListResponseDto> findGroupUsersByAllGroup() {
        return List.of();
    }

//    필터링




}
