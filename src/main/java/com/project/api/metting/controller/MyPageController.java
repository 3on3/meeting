package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.request.RemoveUserDto;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupQueryService;
import com.project.api.metting.service.UserMyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



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
        ChatRoomResponseDto chatRoomList = chatRoomService.findChatById(tokenInfo.getUserId());
        return ResponseEntity.ok(chatRoomList);
    }

// - 프로필 이미지

    // 특정 유저의 프로필 정보를 반환하는 엔드포인트
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userId,
                                                      @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        // 유저 ID로 프로필을 조회
//        UserProfile userProfile = userMyPageService.getUserProfile(tokenInfo.getUserId());
            UserProfile userProfile = userMyPageService.getUserProfile(userId);

        if (userProfile != null) {
            return new ResponseEntity<>(userProfile, HttpStatus.OK); // 프로필이 존재하면 반환
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 프로필이 없으면 404 반환
    }

    // 특정 유저의 프로필 이미지를 반환하는 엔드포인트
    @GetMapping("/profileImage/{userId}")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable String userId) {
        // 유저 ID로 프로필을 조회
        UserProfile userProfile = userMyPageService.getUserProfile(userId);

        if (userProfile != null && userProfile.getProfileImg() != null) { // 프로필과 이미지 경로가 존재하면
            byte[] image = loadProfileImage(userProfile.getProfileImg()); // 이미지 파일을 로드
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg");  // 이미지 타입을 설정 (필요시 변경)
            return new ResponseEntity<>(image, headers, HttpStatus.OK); // 이미지와 함께 응답 반환
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 이미지가 없으면 404 반환
    }

    // 특정 유저의 프로필 이미지 업데이트
    @PostMapping("/profileImage/update/{userId}")
    public ResponseEntity<String> updateUserProfileImage(@PathVariable String userId,
                                                         @RequestParam("file") MultipartFile file) {
        try {
            // 프로필 이미지를 업데이트
            userMyPageService.updateUserProfileImage(userId, file);
            return new ResponseEntity<>("Profile image updated successfully", HttpStatus.OK); // 성공 메시지 반환
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update profile image", HttpStatus.INTERNAL_SERVER_ERROR); // 에러 발생 시 에러 메시지 반환
        }
    }

    // 파일 경로를 통해 이미지를 로드하는 메서드
    private byte[] loadProfileImage(String filePath) {
        try {
            // 파일 경로를 Path 객체로 변환
            Path path = Paths.get(filePath);

            // 파일이 존재하는지 확인
            if (Files.exists(path)) {
                // 파일을 읽어 바이트 배열로 반환
                return Files.readAllBytes(path);
            } else {
                // 파일이 존재하지 않으면 로그를 남기고 예외를 던지거나 기본 이미지를 반환할 수 있음
                log.error("File not found at path: " + filePath);
                // 기본 이미지를 제공할 수 있는 경우, 기본 이미지를 바이트 배열로 반환하거나 예외 처리
                throw new IOException("File not found at path: " + filePath);
            }

        } catch (IOException e) {
            // 파일 읽기 중 오류가 발생한 경우 예외를 처리
            log.error("Error loading file from path: " + filePath, e);
            // 필요에 따라 기본 이미지를 반환하거나, 빈 배열 또는 예외를 던짐
            return new byte[0];
        }
    }

// - 기본 정보 조회 (프로필 이미지 제외)
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
        log.info("userInfo = {}", userInfo);
        return ResponseEntity.ok(userInfo);
    }

    // 유저 정보 수정
    @PutMapping("/userInfo/update")
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