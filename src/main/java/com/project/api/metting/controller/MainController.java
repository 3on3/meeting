package com.project.api.metting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MainController {


//    미팅 리스트 전체 조회
    @GetMapping("/")
    public ResponseEntity<?> getMeetingList(){
        return ResponseEntity.ok().body("");
    }



//필터링
    @GetMapping("/filter")
    public ResponseEntity<?> mainPage() {

        return ResponseEntity.ok().body("");
    }
}
