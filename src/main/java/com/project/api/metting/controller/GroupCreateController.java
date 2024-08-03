package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.service.GroupCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.project.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class GroupCreateController {
    private final GroupCreateService groupCreateService;



    @PostMapping("/create")
    public ResponseEntity<?> GroupCreate(@RequestBody GroupCreateDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupCreateService.createGroup(dto, tokenInfo);
            return ResponseEntity.ok().body("그룹 생성에 성공하였습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 생성에 실패하였습니다. 다시 시도해주세요.");
        }
    }
}
