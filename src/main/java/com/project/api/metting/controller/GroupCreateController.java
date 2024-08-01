package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.service.GroupCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("save user info - {}", dto.getGroupName());
        groupCreateService.createGroup(dto, tokenInfo);
        return ResponseEntity.ok().body("saved success");
    }
}
