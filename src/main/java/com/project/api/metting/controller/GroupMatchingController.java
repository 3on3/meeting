package com.project.api.metting.controller;

import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
     * @param groupMatchingRequestDto - 히스토리 정보에 추가될 리퀘스트 정보 dto
     * @return - 요청 처리 body
     */
    @PostMapping("/createRequest")
    public ResponseEntity<String> createRequest(@RequestBody GroupMatchingRequestDto groupMatchingRequestDto) {
        // 1. 히스토리 생성 요청
        groupMatchingService.createHistory(groupMatchingRequestDto);


        return ResponseEntity.status(HttpStatus.CREATED).body("매칭 신청이 성공적으로 생성되었습니다.");
    }

    /**
     * 매칭 요청 리스트 열람(주최자 기준)
     * @param groupId - 주최자 그룹 아이디
     * @return - 신청자 리스트 반환
     */
    @GetMapping("/response")
    public List<GroupResponseDto> matchingResponse(@RequestParam String groupId) {
//        GroupMatchingResponseDto responseDto = new GroupMatchingResponseDto(groupId);

//        log.debug("그룹아이디", responseDto.getGroupId());
        return groupMatchingService.viewRequestList(groupId);

    }
}
