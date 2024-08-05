package com.project.api.metting.service;



import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.response.GroupMatchingResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.repository.GroupMatchingHistoriesCustomImpl;
import com.project.api.metting.repository.GroupMatchingHistoriesRepository;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMatchingService {

    private  final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private  final GroupRepository groupRepository;
    private final GroupMatchingHistoriesCustomImpl groupMatchingHistoriesCustomImpl;

    /**
     * 그룹 - 그룹 채팅 신청 버튼 클릭시 히스토리 생성하는 함수
     * @param groupMatchingRequestDto - 신청자 그룹, 주최자 그룹 이름 정보
     */
    public void createHistory (GroupMatchingRequestDto groupMatchingRequestDto){


        // 신청자 그룹
        Group requestGroup = groupRepository.findById(groupMatchingRequestDto.getRequestGroupId()).orElse(null);
        // 주최자 그룹
        Group responseGroup = groupRepository.findById(groupMatchingRequestDto.getResponseGroupId()).orElse(null);


        // 같은 그룹 구성으로 신청내역이 있는지
        boolean exists = groupMatchingHistoriesRepository.existsByResponseGroupAndRequestGroup(responseGroup, requestGroup)
                || groupMatchingHistoriesRepository.existsByResponseGroupAndRequestGroup(requestGroup, responseGroup);
        // 이미 신청내역이 있는 경우 히스토리 생성 하지 않음
        if(exists){
            log.error("이미 신청된 그룹");
            return;
        }

        // 히스토리 생성
        GroupMatchingHistory groupMatchingHistory = GroupMatchingHistory.builder()
                .requestGroup(requestGroup)
                .responseGroup(responseGroup)
                .build();

        groupMatchingHistoriesRepository.save(groupMatchingHistory);

    }


    /**
     * 히스토리 process 컬럼을 invited -> matched 로 수정하는 함수
     * @param id - 수정할 히스토리의 아이디
     */
    public void processingHistory (String id){
        GroupMatchingHistory matchingHistory = groupMatchingHistoriesRepository.findById(id).orElse(null);

        if(matchingHistory.getProcess().equals(GroupProcess.MATCHED)){
            log.error("이미 매칭됨");
        }
        matchingHistory.setProcess(GroupProcess.MATCHED);
    }

    public List<Group> viewRequestList(String groupId) {

        List<GroupMatchingHistory> histories = groupMatchingHistoriesCustomImpl.findByResponseGroupId(groupId);

        List<Group> groups = histories.stream().map(GroupMatchingHistory::getResponseGroup).collect(Collectors.toList());

        return groups;
    }
}
