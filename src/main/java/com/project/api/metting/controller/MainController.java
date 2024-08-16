package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MainController {

    private final MainService mainService;


    //    미팅 리스트 전체 조회
    @GetMapping("/main/{pageNo}")
    public ResponseEntity<?> getMeetingList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,@PathVariable int pageNo) {

        Page<MainMeetingListResponseDto> meetingList = null;
        try {
            meetingList = mainService.getMeetingList(tokenUserInfo.getEmail(), pageNo);
        } catch (Exception e) {
            log.error("Error occurred while fetching meeting list", e);
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(meetingList);
    }

    //    미팅 리스트 전체 조회
    @PostMapping("/main/filter")
    public ResponseEntity<?> getMeetingList(@RequestBody MainMeetingListFilterDto dto) {
        log.info(dto.toString());
        Page<MainMeetingListResponseDto> meetingList = null;
        try {
            meetingList = mainService.postMeetingList(dto);
        } catch (Exception e) {
            log.error("Error occurred while fetching meeting list", e);
                throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(meetingList);
    }


}
