package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 현재 로그인된 사용자의 프로필 정보를 조회
     * @param tokenUserInfo - 현재 로그인된 사용자 정보
     * @return - 사용자 프로필 정보
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal TokenProvider.TokenUserInfo tokenUserInfo) {

        try {
            UserProfile userProfile = userProfileService.getUserProfile(tokenUserInfo.getUserId());
            log.info("profile img info - {}", userProfile.getProfileImg());
            return ResponseEntity.ok(userProfile);
        } catch (IllegalStateException e ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 정보 조회에 실패하였습니다.");
        }
    }
}

