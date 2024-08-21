package com.project.api.metting.service;


import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
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



        setMatchingStatusRequesting(groupsByUserEmail,mainMeetingListResponseDtos);
        setMatchingStatusResponse(groupsByUserEmail,mainMeetingListResponseDtos);

        return mainMeetingListResponseDtos;
    }


    /**
     * 매칭 상태를 Requesting으로 수정
     * @param groupsByUserEmail - 이메일로 그룹 조회
     * @param mainMeetingListResponseDtos - 메인에 로딩되는 그룹들 dto
     */
    private void setMatchingStatusRequesting( List<Group> groupsByUserEmail, Page<MainMeetingListResponseDto>  mainMeetingListResponseDtos) {
        // 3. 로그인한 유저의 모든 히스토리를 담을 리스트
        List<GroupMatchingHistory> allRequestHistories = new ArrayList<>();

        // 4. 로그인한 유저가 매칭 신청한 모든 히스토리를 담을 리스트
        List<String> allResponseHistoriesId = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            // 5-1. 히스토리 중 리퀘스트 아이디가 일치하는 히스토리
            allRequestHistories.addAll(groupMatchingHistoriesRepository.findAllByRequestGroup(group)) ;
            // 5-2. 로그인한 유저에게 매칭신청을 받은 그룹들 아이디
            List<String> collect = allRequestHistories.stream().map(groupMatchingHistory -> groupMatchingHistory.getResponseGroup().getId() ).collect(Collectors.toList());
            allResponseHistoriesId.addAll(collect);
        }
        log.info("allResponseHistoriesId = {}", allResponseHistoriesId);

        // 6. mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
            if (allResponseHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.REQUESITNG);
            }
        });
    }
    /**
     * 매칭 상태를 Response으로 수정
     * @param groupsByUserEmail - 이메일로 그룹 조회
     * @param mainMeetingListResponseDtos - 메인에 로딩되는 그룹들 dto
     */
    private void setMatchingStatusResponse( List<Group> groupsByUserEmail, Page<MainMeetingListResponseDto>  mainMeetingListResponseDtos) {

        // 3. 로그인한 유저의 모든 히스토리를 담을 리스트
        List<GroupMatchingHistory> allResponseHistories = new ArrayList<>();

        // 4. 로그인한 유저가 매칭 신청받은 모든 히스토리를 담을 리스트
        List<String> allRequestHistoriesId = new ArrayList<>();

        for (Group group : groupsByUserEmail) {
            // 5-1. 히스토리 중 리스폰스 아이디가 일치하는 히스토리
            allResponseHistories.addAll(groupMatchingHistoriesRepository.findAllByResponseGroup(group)) ;
            // 5-2. 로그인한 유저에게 매칭신청을 한 그룹들 아이디
            List<String> collect = allResponseHistories.stream().map(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId() ).collect(Collectors.toList());
            allRequestHistoriesId.addAll(collect);
        }
        log.info("allRequestHistoriesId = {}", allRequestHistoriesId);

        // 6. mainMeetingListResponseDtos 리스트의 각 DTO에 대해 매칭 히스토리 존재 여부를 설정
        mainMeetingListResponseDtos.forEach(dto -> {
            if (allRequestHistoriesId.contains(dto.getId())) {
                dto.setMatchingStatus(MatchingStatus.RESPONSE);
            }
        });
    }



}
