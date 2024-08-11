package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.request.RemoveUserDto;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupQueryService;
import com.project.api.metting.service.UserMyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final ChatRoomService chatRoomService;
    private final UserMyPageService userMyPageService;

    // 내가 속한 그룹 조회
    @GetMapping("/mygroup")
    public ResponseEntity<?> getMyGroups(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
        List<GroupResponseDto> groups = groupQueryService.getGroupsByUserEmail(tokenInfo.getEmail());
        return ResponseEntity.ok(groups);
    }

    // 내가 속한 채팅 조회
    @GetMapping("/mychat")
    public ResponseEntity<?> getMyChat(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
        ChatRoomResponseDto chatRoomList = chatRoomService.findChatById(tokenInfo.getEmail());
        return ResponseEntity.ok(chatRoomList);
    }

    /**
     * 로그인한 사용자의 프로필 정보 반환
     *
     * @param tokenInfo 현재 인증된 사용자의 정보
     * @return UserMyPageDto 객체를 담은 ResponseEntity
     */

    // 유저 정보 조회
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal TokenUserInfo tokenInfo) {

        UserMyPageDto userInfo = userMyPageService.getUserInfo(tokenInfo.getUserId());
        System.out.println("userInfo =" + userInfo);
        return ResponseEntity.ok(userInfo);
    }

    // 유저 정보 수정
    @PutMapping("/")
    public ResponseEntity<UserMyPageDto> updateUser(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                                    @RequestBody UserUpdateRequestDto updateDto) {

        UserMyPageDto updatedUser = userMyPageService.updateUserFields(tokenInfo.getUserId(), updateDto);
        System.out.println("updatedUser =" + updatedUser);

        return ResponseEntity.ok(updatedUser);
    }

    // 유저 비밀번호 변경
    @PatchMapping("/check-pass")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                            @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            userMyPageService.changePassword(tokenInfo.getUserId(), changePasswordDto);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다.");
        }
    }

    /**
     * 회원 탈퇴와 관련된 모든 작업을 처리하는 엔드포인트.
     *
     * @param dto 사용자 요청 데이터를 담은 RemoveUserDto
     * @return 작업 결과를 나타내는 응답
     */
    @PostMapping("/removeUser")
    public ResponseEntity<?> handleRemoveUser(@RequestBody RemoveUserDto dto) {
        try {
            // action에 따라 작업을 구분하여 처리
            switch (dto.getAction()) {
                case "sendCode":
                    // 인증 코드 전송 로직
                    userMyPageService.sendRemovalVerificationCode(dto.getEmail());
                    return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");

                case "removeUser":
                    // 회원 탈퇴 로직
                    userMyPageService.removeUserWithVerification(dto.getEmail(), dto.getCode(), dto.getPassword());
                    return ResponseEntity.ok("회원 탈퇴가 성공적으로 완료되었습니다.");

                default:
                    return ResponseEntity.badRequest().body("잘못된 요청입니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}