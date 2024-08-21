package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MainController {

    private final MainService mainService;


    //미팅 리스트 전체 조회
    @GetMapping("/main")
    public ResponseEntity<?> getMeetingList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                            @RequestParam int pageNo,
                                            @RequestParam (required = false)String gender,
                                            @RequestParam (required = false) String region,
                                            @RequestParam (required = false) Integer personnel
                                            ) {

        Page<MainMeetingListResponseDto> meetingList = null;
        try {
            meetingList = mainService.getMeetingList(tokenUserInfo.getEmail(), pageNo,gender,region,personnel);
        } catch (Exception e) {
            log.error("Error occurred while fetching meeting list", e);
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(meetingList);
    }

}
