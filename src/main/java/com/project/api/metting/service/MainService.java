package com.project.api.metting.service;


import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.repository.GroupMatchingHistoriesRepository;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


        // 이미 매칭 신청 중인 그룹이예요.
        // 1. 해당 사용자가 속한 그룹들을 가져옴
        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);
        log.info("groupsByUserEmail = {}", groupsByUserEmail);

        // 2. 사용가 속한 그룹의 아이디 셋 (history기준 requestId)
        Set<String> userGroupIdsWithHistory = new HashSet<>();
//        groupsByUserEmail.forEach(group -> userGroupIdsWithHistory.add(group.getId()));

        // 3. 로그인한 유저의 모든 히스토리를 담을 리스트
        List<GroupMatchingHistory> allRequestHistories = new ArrayList<>();
        // 4. 로그인한 유저가 매칭 신청한 모든 히스토리를 담을 리스트
        List<String> allResponseHistoriesId = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            // 5-1. 히스토리 중 리퀘스트 아이디가 일치하는 히스토리
            allRequestHistories.addAll(groupMatchingHistoriesRepository.findAllByRequestGroup(group)) ;
//            log.info("groupMatchingHistoriesRepository.findAllByRequestGroup(group) = {}", groupMatchingHistoriesRepository.findAllByRequestGroup(group));
//            log.info("allRequestHistories = {}", allRequestHistories);
            // 5-2. 로그인한 유저에게 매칭신청을 받은 그룹들 아이디
            List<String> collect = allRequestHistories.stream().map(groupMatchingHistory -> groupMatchingHistory.getResponseGroup().getId() ).collect(Collectors.toList());
//            List<GroupHistoryResponseDto> groupHistories = histories.stream()
//                    .map(GroupHistoryResponseDto::new)
//                    디collect(Collectors.toList());
            allResponseHistoriesId.addAll(collect);
//            log.info("collect = {}", collect);

        }
        log.info("allResponseHistoriesId = {}", allResponseHistoriesId);
        // mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
//            allResponseHistories.forEach(groupHistoryResponseDto -> hi);
            if (allResponseHistoriesId.contains(dto.getId())) {
                dto.setExistMatchingHistory(true);
            } else {
                dto.setExistMatchingHistory(false);
            }
        });

        return mainMeetingListResponseDtos;
    }


    //    group 필터링
    public Page<MainMeetingListResponseDto> postMeetingList(MainMeetingListFilterDto dto) {

        return groupRepository.filterGroupUsersByAllGroup(dto);
    }


}
