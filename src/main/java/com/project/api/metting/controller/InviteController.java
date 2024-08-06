package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.metting.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invite")
public class InviteController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<String> joinGroupWithInvite(@RequestParam String code, @AuthenticationPrincipal TokenProvider.TokenUserInfo tokenInfo) {
        try {
            groupService.joinGroupWithInviteCode(code, tokenInfo);
            return ResponseEntity.ok("가입이 성공적으로 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
