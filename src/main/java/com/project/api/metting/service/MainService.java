package com.project.api.metting.service;


import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.GroupHistoryResponseDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.repository.GroupMatchingHistoriesRepository;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MainService {
    public final GroupRepository groupRepository;
    private final GroupMatchingService groupMatchingService;
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;


    //    Group 전체 조회
    public Page<MainMeetingListResponseDto> getMeetingList(String email, int pageNo) {
        PageRequest pageable = PageRequest.of(pageNo - 1, 5);
        Page<MainMeetingListResponseDto> mainMeetingListResponseDtos = groupRepository.findGroupUsersByAllGroup(pageable);

        // 해당 사용자가 속한 그룹들을 가져옴
        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);

        // 매칭 이력이 있는 그룹들의 아이디를 수집
        Set<String> userGroupIdsWithHistory = new HashSet<>();
        groupsByUserEmail.forEach(group -> userGroupIdsWithHistory.add(group.getId()));

        // 모든 그룹의 히스토리를 담을 리스트
        List<GroupHistoryResponseDto> allGroupHistories = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            List<GroupMatchingHistory> histories = groupMatchingHistoriesRepository.findAllByRequestGroup(group);
            List<GroupHistoryResponseDto> groupHistories = histories.stream()
                    .map(GroupHistoryResponseDto::new)
                    .collect(Collectors.toList());
            allGroupHistories.addAll(groupHistories);
        }

        // mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
            if (userGroupIdsWithHistory.contains(dto.getId())) {
                dto.setExistMatchingHistory(false);
            } else {
                dto.setExistMatchingHistory(true);
            }
        });

        return mainMeetingListResponseDtos;
    }


    //    group 필터링
    public Page<MainMeetingListResponseDto> postMeetingList(MainMeetingListFilterDto dto) {

        return groupRepository.filterGroupUsersByAllGroup(dto);
    }


}
