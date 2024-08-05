package com.project.api.metting.service;


import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.GroupMatchingHistoryRequestDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.repository.GroupMatchingHistoriesRepository;
import com.project.api.metting.repository.GroupRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMatchingHistoriesService {

    private  final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private  final GroupRepository groupRepository;

    /**
     * 그룹 - 그룹 채팅 신청 버튼 클릭시 히스토리 생성하는 함수
     * @param groupMatchingHistoryRequestDto - 신청자 그룹, 주최자 그룹 이름 정보
     */
    public void createHistory (GroupMatchingHistoryRequestDto groupMatchingHistoryRequestDto){


        // 신청자 그룹
        Group requestGroup = groupRepository.findById(groupMatchingHistoryRequestDto.getRequestGroupId()).orElse(null);
        // 주최자 그룹
        Group responseGroup = groupRepository.findById(groupMatchingHistoryRequestDto.getResponseGroupId()).orElse(null);
        boolean exists = groupMatchingHistoriesRepository.existByResponseGroupAndRequestGroup(responseGroup, requestGroup)
                || groupMatchingHistoriesRepository.existByResponseGroupAndRequestGroup(requestGroup, responseGroup);


        // 히스토리 생성
        GroupMatchingHistory groupMatchingHistory = GroupMatchingHistory.builder()
                .requestGroup(requestGroup)
                .responseGroup(responseGroup)
                .build();


        if(exists){
            log.error("이미 신청된 그룹");
            return;
        }

        groupMatchingHistoriesRepository.save(groupMatchingHistory);

    }


    /**
     * 히스토리 process 컬럼을 invited -> matched 로 수정하는 함수
     * @param id - 수정할 히스토리의 아이디
     */
    public void processingHistory (String id){
        GroupMatchingHistory matchingHistory = groupMatchingHistoriesRepository.findById(id).orElse(null);

        matchingHistory.setProcess(GroupProcess.MATCHED);
    }
}
