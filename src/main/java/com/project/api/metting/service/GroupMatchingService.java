package com.project.api.metting.service;



import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.project.api.exception.GroupMatchingFailException;
import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.request.GroupRequestDto;
import com.project.api.metting.dto.response.GroupMatchingResponseDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Alarm;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMatchingService {

    private  final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private  final GroupRepository groupRepository;
    private final GroupMatchingHistoriesCustomImpl groupMatchingHistoriesCustomImpl;
    private final GroupRepositoryCustomImpl groupRepositoryCustomImpl;
    private final AlarmRepository alarmRepository;

    /**
     * 그룹 - 그룹 채팅 신청 버튼 클릭시 히스토리 생성하는 함수
     * @param groupMatchingRequestDto - 신청자 그룹, 주최자 그룹 이름 정보
     */
    public void createHistory (GroupMatchingRequestDto groupMatchingRequestDto){
        try {
            // 신청자 그룹
            Group requestGroup = groupRepository.findById(groupMatchingRequestDto.getRequestGroupId()).orElse(null);
            // 주최자 그룹
            Group responseGroup = groupRepository.findById(groupMatchingRequestDto.getResponseGroupId()).orElse(null);

            // [신청 불가 요건]
            boolean exist = groupMatchingHistoriesRepository.existsByResponseGroupAndRequestGroup(responseGroup, requestGroup) || groupMatchingHistoriesRepository.existsByResponseGroupAndRequestGroup(requestGroup, responseGroup);
            if(exist){
                GroupMatchingHistory byResponseGroupAndRequestGroup = groupMatchingHistoriesRepository.findByResponseGroupAndRequestGroup(responseGroup, requestGroup);
                GroupMatchingHistory byResponseGroupAndRequestGroup1 = groupMatchingHistoriesRepository.findByResponseGroupAndRequestGroup(requestGroup, responseGroup);
                // 1-1. 이미 매칭 신청
                if(byResponseGroupAndRequestGroup.getProcess() == GroupProcess.INVITING || byResponseGroupAndRequestGroup1.getProcess() == GroupProcess.INVITING){
                    throw new GroupMatchingFailException("이미 매칭 신청한 그룹입니다.", HttpStatus.CONFLICT);
                }
                // 1-2. 매칭된 경우
                if(byResponseGroupAndRequestGroup.getProcess() == GroupProcess.MATCHED || byResponseGroupAndRequestGroup1.getProcess() == GroupProcess.MATCHED){
                    throw new GroupMatchingFailException("이미 매칭된 그룹입니다.", HttpStatus.CONFLICT);
                }
                // 1-3. 매칭 거절된경우
                if(byResponseGroupAndRequestGroup.getProcess() == GroupProcess.DENIED|| byResponseGroupAndRequestGroup1.getProcess() == GroupProcess.DENIED){
                    throw new GroupMatchingFailException("이미 매칭 거절된 그룹입니다.", HttpStatus.CONFLICT);
                }
            }

            // 2. 인원 수 다를 경우
            if(requestGroup.getMaxNum() != responseGroup.getMaxNum()){
                throw new GroupMatchingFailException("인원 수가 다릅니다.", HttpStatus.BAD_REQUEST);
            }
            // 3. 지역 다를 경우
            if(!requestGroup.getGroupPlace().equals(responseGroup.getGroupPlace()) ){
                throw new GroupMatchingFailException("희망지역이 다릅니다.", HttpStatus.BAD_REQUEST);
            }
            // 4. 이미 다른 그룹과 매칭된 그룹일 경우
            boolean isMatchedByResponse = groupMatchingHistoriesRepository.existsByResponseGroupAndProcess(responseGroup, GroupProcess.MATCHED);
            boolean isMatchedByRequest = groupMatchingHistoriesRepository.existsByRequestGroupAndProcess(responseGroup, GroupProcess.MATCHED);
            if(isMatchedByResponse || isMatchedByRequest){
                throw new GroupMatchingFailException("이미 다른 그룹과 매칭된 그룹입니다.", HttpStatus.CONFLICT);
            }



            // 히스토리 생성
            GroupMatchingHistory groupMatchingHistory = GroupMatchingHistory.builder()
                    .requestGroup(requestGroup)
                    .responseGroup(responseGroup)
                    .build();

            // 히스토리 저장
            GroupMatchingHistory savedHistory = groupMatchingHistoriesRepository.save(groupMatchingHistory);

            // 알람 테이블에도 추가
            Alarm alarm = Alarm.builder()
                    .groupMatchingHistory(savedHistory)
                    .build();

            alarmRepository.save(alarm);

        } catch (Exception e){
            throw new GroupMatchingFailException("예외 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




    /**
     * 매칭 수락 또는 거절 프로세싱
     * @param groupMatchingResponseDto - 수정할 히스토리의 아이디
     * @param process - 프로세스 상태 INVITED, MATCHED, DENIED
     * @param message - 예외처리 메세지
     */
    private GroupProcess processingRequest (GroupMatchingResponseDto groupMatchingResponseDto, GroupProcess process, String message){
        try{
            List<GroupMatchingHistory> histories = groupMatchingHistoriesCustomImpl.findByResponseGroupId(groupMatchingResponseDto.getResponseGroupId());
            GroupMatchingHistory groupMatchingHistory = histories.stream().filter(history -> history.getRequestGroup().getId().equals(groupMatchingResponseDto.getRequestGroupId())).findFirst().orElseThrow();

            if(groupMatchingHistory.getProcess().equals(process)){
                throw new GroupMatchingFailException(message, HttpStatus.BAD_REQUEST);
            }
            groupMatchingHistory.setProcess(process);
            groupMatchingHistoriesRepository.save(groupMatchingHistory);
            return process;

        } catch (NullPointerException e){
            throw new GroupMatchingFailException("히스토리에 일치하는 groupId 가 없습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e){
            throw new GroupMatchingFailException("예외 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 매칭 수락
     * - 히스토리 process 컬럼을 invited -> matched 로 수정하는 함수
     * @param groupMatchingResponseDto - 수정할 히스토리의 아이디
     */
    public GroupProcess acceptRequest (GroupMatchingResponseDto groupMatchingResponseDto){
        String message = "이미 매칭된 그룹입니다.";
        GroupProcess process = processingRequest(groupMatchingResponseDto, GroupProcess.MATCHED, message);
        return process;
    }

    /**
     * 매칭 거절
     * - 히스토리 process 컬럼을 invited -> denied 로 수정하는 함수
     * @param groupMatchingResponseDto - 수정할 히스토리의 아이디
     */
    public GroupProcess denyRequest (GroupMatchingResponseDto groupMatchingResponseDto){
        String message = "이미 매칭 거절된 그룹입니다.";
        GroupProcess process = processingRequest(groupMatchingResponseDto, GroupProcess.DENIED, message);
        return process;
    }

    /**
     * 주최자 기준 신청자 그룹 리스트 조회 함수
     * @param groupId - 주최자 그룹 아이디
     * @return - 신청자 그룹 리스트 Dto 반환
     */
    @Transactional(readOnly = true)
    public List<GroupRequestDto> viewRequestList(String groupId) {

        List<GroupMatchingHistory> histories = groupMatchingHistoriesCustomImpl.findByResponseGroupId(groupId);
        List<GroupMatchingHistory> collect = histories.stream()
                .filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.INVITING))
                .collect(Collectors.toList());
        return collect.stream()
                .map(GroupMatchingHistory::getRequestGroup)
                .map(groupRepositoryCustomImpl::convertToGroupRequestDto)
                .collect(Collectors.toList());

    }


    // 요청 그룹, 주최자 그룹으로 히스토리 조회
    public GroupMatchingHistory findByResponseGroupAndRequestGroup(Group findResponseGroup,Group findRequestGroup){
        return groupMatchingHistoriesRepository.findByResponseGroupAndRequestGroup(findResponseGroup, findRequestGroup);
    }
    // 요청 그룹아이디로 히스토리 조회
    public List<GroupMatchingHistory> findByResponseGroup(Group findRequestGroup){
        return groupMatchingHistoriesRepository.findAllByRequestGroup(findRequestGroup);
    }
}
