package com.project.api.metting.service;


import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MainService {
    public final GroupRepository groupRepository;


//    Group 전체 조회
    public Page<MainMeetingListResponseDto> getMeetingList(String email,int pageNo) {


        PageRequest pageable = PageRequest.of(pageNo - 1, 5);
        Page<MainMeetingListResponseDto> mainMeetingListResponseDtos = groupRepository.findGroupUsersByAllGroup(pageable);
        // ================= setExistMatchingHistory
        // 해당 사용자가 속한 그룹들을 가져옴
        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);
        // groupsByUserEmail 리스트의 ID들을 Set으로 변환
        Set<String> userGroupIds = groupsByUserEmail.stream()
                .map(Group::getId)
                .collect(Collectors.toSet());
        // groupUsersByAllGroup 리스트를 순회하며 ID를 비교하여 isExistMatchingHistory 설정
        for (MainMeetingListResponseDto dto : mainMeetingListResponseDtos) {
            if (userGroupIds.contains(dto.getId())) {
                dto.setExistMatchingHistory(true);
            }
        }
        return mainMeetingListResponseDtos;
    }


//    group 필터링
    public Page<MainMeetingListResponseDto> postMeetingList(MainMeetingListFilterDto dto) {

        return  groupRepository.filterGroupUsersByAllGroup(dto);
    }

//    나이 계산하기

}
