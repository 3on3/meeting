package com.project.api.metting.controller;

import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MainController {

    private final MainService mainService;


//    미팅 리스트 전체 조회
    @GetMapping("/main")
    public ResponseEntity<?> getMeetingList() {

//        List<Group> meetingList = mainService.getMeetingList();

//        log.info("meetingList: {}", meetingList);
        List<MainMeetingListResponseDto> meetingList = mainService.getMeetingList();

        return ResponseEntity.ok().body(meetingList);
    }



//필터링
    @GetMapping("/filter")
    public ResponseEntity<?> mainPage() {

        return ResponseEntity.ok().body("");
    }
}
