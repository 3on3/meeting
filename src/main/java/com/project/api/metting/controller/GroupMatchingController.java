package com.project.api.metting.controller;

import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.response.GroupMatchingResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group/matching")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class GroupMatchingController {
    private final GroupMatchingService groupMatchingService;

    /**
     * 매칭 요청(신청자 역할)
     * @param requestDto
     */
    @PostMapping("/request")
    public void matchingRequest(GroupMatchingRequestDto requestDto) {

        // 히스토리 생성 요청
        groupMatchingService.createHistory(requestDto);

    }

    /**
     * 매칭 요청 열람(주최자 기준)
     * @param responseDto - 주최자 그룹 아이디
     * @return - 신청자 리스트 반환
     */
    @GetMapping("/response")
    public List<Group> matchingResponst( @RequestParam String groupId) {
//        GroupMatchingResponseDto responseDto = new GroupMatchingResponseDto(groupId);

//        log.debug("그룹아이디", responseDto.getGroupId());
        return groupMatchingService.viewRequestList(groupId);

    }
}
