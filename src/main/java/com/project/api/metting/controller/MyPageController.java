package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.service.GroupQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 그룹을 조회하는 컨트롤러임
 */
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MyPageController {

    private final GroupQueryService groupQueryService;

//    @GetMapping("/mygroup")
//    public ResponseEntity<List<GroupResponseDto>> getMyGroups(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
//        List<GroupResponseDto> groups = groupQueryService.getGroupsByUserEmail(tokenInfo.getEmail());
//        return ResponseEntity.ok(groups);
//    }
}
