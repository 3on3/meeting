package com.project.api.metting.service;


import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.entity.MatchingStatus;
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

    /**
     * @param email 사용자의 이메일
     * @param pageNo 요청한 페이지 번호
     * @param gender 필터링할 성별 (선택 사항)
     * @param region 필터링할 지역 (선택 사항)
     * @param personnel 필터링할 인원수 (선택 사항)
     * @return 필터링된 미팅 리스트의 페이지
     */
    public Page<MainMeetingListResponseDto> getMeetingList(String email, int pageNo,String gender,String region,Integer personnel) {
        PageRequest pageable = PageRequest.of(pageNo - 1, 4);
        Page<MainMeetingListResponseDto> mainMeetingListResponseDtos = groupRepository.findGroupUsersByAllGroup(pageable,gender,region,personnel,email);


        // 이미 매칭 신청 중인 그룹이예요.
        // 1. 해당 사용자가 속한 그룹들을 가져옴
        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);


//        setMatchingStatus(groupsByUserEmail,mainMeetingListResponseDtos,"Request");
//        setMatchingStatus(groupsByUserEmail,mainMeetingListResponseDtos,"Response");
        setMatchingStatusRequesting(groupsByUserEmail,mainMeetingListResponseDtos);
        setMatchingStatusResponse(groupsByUserEmail,mainMeetingListResponseDtos);

        return mainMeetingListResponseDtos;
    }

    /**
     * 메인에 랜더링 되는 그룹들 유저 그룹 기준으로 매칭 상태 설정
     * @param groupsByUserEmail - 유저 이메일 기준 속한 그룹 리스트
     * @param mainMeetingListResponseDtos - 메인에 랜더링 되는 리스트
     * @param method - 신청자 기준인지, 주최자 기준인지 (Request/Response)
     */
    private void setMatchingStatus(List<Group> groupsByUserEmail, Page<MainMeetingListResponseDto>  mainMeetingListResponseDtos, String method){
        // 1. 모든 히스토리를 담을 그룹
        List<GroupMatchingHistory> allHistories = new ArrayList<>();

        List<String> inviteHistoriesId = new ArrayList<>();
        List<String> denyHistoriesId = new ArrayList<>();
        List<String> closedHistoriesId = new ArrayList<>();
        for (Group group : groupsByUserEmail) {

            if(method.equals("Request")){
                allHistories.addAll(groupMatchingHistoriesRepository.findAllByRequestGroup(group)) ;
            } else if (method.equals("Response")){
                allHistories.addAll(groupMatchingHistoriesRepository.findAllByResponseGroup(group)) ;
            }
            // process가 INVITING인 히스토리 아이디 리스트
            List<String> inviteCollect = allHistories.stream().filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.INVITING)).map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId() ).collect(Collectors.toList());
            // process가 DENY인 히스토리 아이디 리스트
            List<String> denyCollect = allHistories.stream().filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.DENIED)).map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId() ).collect(Collectors.toList());
            // process가 CLOSED인 히스토리 아이디 리스트
            List<String> closedCollect = allHistories.stream().filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.CLOSED)).map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId() ).collect(Collectors.toList());

            // 아이디만 collect 한 리스트
            inviteHistoriesId.addAll(inviteCollect);
            log.info("inviteHistoriesId: {}", inviteHistoriesId);
            denyHistoriesId.addAll(denyCollect);
            log.info("denyHistoriesId: {}", denyHistoriesId);
            closedHistoriesId.addAll(closedCollect);
            log.info("closedHistoriesId: {}", closedHistoriesId);

            mainMeetingListResponseDtos.forEach(dto -> {
                if (inviteHistoriesId.contains(dto.getId())) {
                    if(method.equals("Request")){
                        dto.setMatchingStatus(MatchingStatus.REQUESITNG);
                        log.info("inviteHistoriesId REQUESITNG: {}", inviteHistoriesId);

                    } else if (method.equals("Response")) {
                        dto.setMatchingStatus(MatchingStatus.RESPONSE);
                        log.info("inviteHistoriesId RESPONSE: {}", inviteHistoriesId);

                    }
                }
                else if (denyHistoriesId.contains(dto.getId())) {
                    dto.setMatchingStatus(MatchingStatus.REQUEST_DENIED);
                }
                else if (closedHistoriesId.contains(dto.getId())) {
                    dto.setMatchingStatus(MatchingStatus.CLOSED);
                }
            });
        }
    }

    /** ================================================================================================================================================
     * 매칭 상태를 매칭 신청 중(Requesting)으로 수정
     * @param groupsByUserEmail - 이메일로 그룹 조회
     * @param mainMeetingListResponseDtos - 메인에 로딩되는 그룹들 dto
     */
    private void setMatchingStatusRequesting( List<Group> groupsByUserEmail, Page<MainMeetingListResponseDto>  mainMeetingListResponseDtos) {
        // 3. 로그인한 유저의 모든 히스토리를 담을 리스트
        List<GroupMatchingHistory> allRequestHistories = new ArrayList<>();

        // 4. 로그인한 유저가 매칭 신청한 모든 히스토리를 담을 리스트
        List<String> invitingRequestHistoriesId = new ArrayList<>();
        List<String> deniedRequestHistoriesId = new ArrayList<>();
        List<String> closedRequestHistoriesId = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            // 로그인한 유저가 매칭 신청한 모든 히스토리를 담을 리스트
            List<GroupMatchingHistory> histories = groupMatchingHistoriesRepository.findAllByRequestGroup(group);

            // 각 상태별로 히스토리를 필터링하여 수집
            List<String> invitingCollect = histories.stream()
                    .filter(history -> history.getProcess().equals(GroupProcess.INVITING))
                    .map(history -> history.getResponseGroup().getId())
                    .collect(Collectors.toList());

            List<String> deniedCollect = histories.stream()
                    .filter(history -> history.getProcess().equals(GroupProcess.DENIED))
                    .map(history -> history.getResponseGroup().getId())
                    .collect(Collectors.toList());

            List<String> closedCollect = histories.stream()
                    .filter(history -> history.getProcess().equals(GroupProcess.CLOSED))
                    .map(history -> history.getResponseGroup().getId())
                    .collect(Collectors.toList());

            // 각각의 리스트에 올바르게 데이터를 추가
            invitingRequestHistoriesId.addAll(invitingCollect);
            deniedRequestHistoriesId.addAll(deniedCollect);
            closedRequestHistoriesId.addAll(closedCollect);
        }
        log.info("invitingRequestHistoriesId = {}", invitingRequestHistoriesId);
    log.info("deniedRequestHistoriesId = {}", deniedRequestHistoriesId);
    log.info("closedRequestHistoriesId = {}", closedRequestHistoriesId);
        // 6. mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
            if (invitingRequestHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.REQUESITNG);
            }
            if (deniedRequestHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.REQUEST_DENIED);
            }
            if (closedRequestHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.CLOSED);
            }
        });
    }
    /**
     * 매칭 상태를 매칭 신청 받는 중(Response)으로 수정
     * @param groupsByUserEmail - 이메일로 그룹 조회
     * @param mainMeetingListResponseDtos - 메인에 로딩되는 그룹들 dto
     */
    private void setMatchingStatusResponse( List<Group> groupsByUserEmail, Page<MainMeetingListResponseDto>  mainMeetingListResponseDtos) {

        // 3. 로그인한 유저의 모든 히스토리를 담을 리스트
        List<GroupMatchingHistory> allResponseHistories = new ArrayList<>();

        // 4. 로그인한 유저가 매칭 신청받은 모든 히스토리를 담을 리스트
        List<String> invitedResponseHistoriesId = new ArrayList<>();
        List<String> denyResponseHistoriesId = new ArrayList<>();
        List<String> closedResponseHistoriesId = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            // 5-1. 히스토리 중 리스폰스 아이디가 일치하는 히스토리
            List<GroupMatchingHistory> responseHistories = groupMatchingHistoriesRepository.findAllByResponseGroup(group);

            // 5-2. 상태별로 필터링 후 수집
            List<String> invitedCollect = responseHistories.stream()
                    .filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.INVITING))
                    .map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId())
                    .collect(Collectors.toList());

            List<String> denyCollect = responseHistories.stream()
                    .filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.DENIED))
                    .map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId())
                    .collect(Collectors.toList());

            List<String> closedCollect = responseHistories.stream()
                    .filter(groupMatchingHistory -> groupMatchingHistory.getProcess().equals(GroupProcess.CLOSED))
                    .map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId())
                    .collect(Collectors.toList());

            // 6. 각각의 리스트에 해당하는 상태의 데이터 추가
            invitedResponseHistoriesId.addAll(invitedCollect);
            denyResponseHistoriesId.addAll(denyCollect);
            closedResponseHistoriesId.addAll(closedCollect);
        }

        log.info("invitedResponseHistoriesId = {}", invitedResponseHistoriesId);
        log.info("deniedResponseHistoriesId = {}", denyResponseHistoriesId);
        log.info("closedResponseHistoriesId = {}", closedResponseHistoriesId);

        // 6. mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
            if (invitedResponseHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.RESPONSE);
            }
            if (denyResponseHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.RESPONSE_DENY);
            }
            if (closedResponseHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.CLOSED);
            }
        });
    }


}
