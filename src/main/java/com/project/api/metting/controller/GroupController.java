package com.project.api.metting.controller;


import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.dto.request.GroupJoinRequestDto;
import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.service.GroupMatchingService;
import com.project.api.metting.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.project.api.auth.TokenProvider.*;


/**
 * 그룹을 생성하는 컨트롤러임
 */
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class GroupController {
    private final GroupService groupService;



    @PostMapping("/create")
    public ResponseEntity<?> GroupCreate(@RequestBody GroupCreateDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.createGroup(dto, tokenInfo);
            return ResponseEntity.ok().body("그룹 생성에 성공하였습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 생성에 실패하였습니다. 다시 시도해주세요.");
        }
    }


    @PostMapping("/join")
    public ResponseEntity<?> GroupJoin(@RequestBody GroupJoinRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.joinGroup(dto, tokenInfo);
            return ResponseEntity.ok().body("가입 신청이 완료되었습니다.");
        } catch (IllegalStateException e ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 참여 신청에 실패하였습니다. 다시 시도해주세요.");
        }
    }


}
