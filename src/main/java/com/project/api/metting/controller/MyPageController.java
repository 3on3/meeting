package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.service.GroupQueryService;
import com.project.api.metting.service.UserMyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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
    private final UserMyPageService userMyPageService;

    //    fetch 내가 속한 그룹
    @GetMapping("/mygroup")
    public ResponseEntity<?> getMyGroups(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
        List<GroupResponseDto> groups = groupQueryService.getGroupsByUserEmail(tokenInfo.getEmail());
        return ResponseEntity.ok(groups);
    }


//    fetch 내가 속한 채팅 - 채팅방 생성되면 작업할 예정(예진)
    @GetMapping("/mychat")
    public ResponseEntity<?> getMyChat(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
        return ResponseEntity.ok("");
    }

    /**
     * 로그인한 사용자의 프로필 정보 반환
     *
     * @param tokenUserInfo 현재 인증된 사용자의 정보
     * @return UserMyPageDto 객체를 담은 ResponseEntity
     */


    // 로그인한 유저 정보 조회
//    @GetMapping("/userInfo")
//    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
//        Optional<UserMyPageDto> userInfo = userMyPageService.getUserInfo(tokenUserInfo.getUserId());
//        if (userInfo.isPresent()) {
//            return ResponseEntity.ok(userInfo.get());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저없음");
//        }
//    }
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        if (tokenUserInfo == null || tokenUserInfo.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user info");
        }

        Optional<UserMyPageDto> userInfo = userMyPageService.getUserInfo(tokenUserInfo.getUserId());
        if (userInfo.isPresent()) {
            return ResponseEntity.ok(userInfo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저없음");
        }
    }

    @PatchMapping("/check-pass")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        // 인증 정보 유효성 확인
        if (tokenUserInfo == null || tokenUserInfo.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유저 정보 유효하지 않음.");
        }

        boolean isChanged = userMyPageService.changePassword(tokenUserInfo.getUserId(), changePasswordDto);
        if(isChanged) {
            return ResponseEntity.ok("비밀번호 변경 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패");
        }
    }
}