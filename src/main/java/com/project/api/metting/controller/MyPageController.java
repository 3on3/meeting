package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.exception.DuplicateNicknameException;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.request.ChangePasswordDto;
import com.project.api.metting.dto.request.MatchedGroupRequestDto;
import com.project.api.metting.dto.request.UserUpdateRequestDto;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.LoginResponseDto;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.repository.UserMyPageRepository;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Email;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MyPageController {

    private final GroupQueryService groupQueryService;
    private final ChatRoomService chatRoomService;
    private final UserMyPageService userMyPageService;
    private final FileUploadService uploadService;
    private final UserMyPageRepository userMyPageRepository;
    private final UserProfileService userProfileService;
    private final UserSignInService userSignInService;
    private final TokenProvider tokenProvider;

    /**
     * 현재 로그인된 사용자의 프로필 정보를 조회
     * @param tokenUserInfo - 현재 로그인된 사용자 정보
     * @return - 사용자 프로필 정보
     */
    @GetMapping("/profileImage")
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


    // 파일 업로드 처리
    @PostMapping("/profileImage/update")
    public ResponseEntity<?> upload(@RequestPart(value = "profileImage") MultipartFile uploadFile,
                                    @AuthenticationPrincipal TokenUserInfo tokenInfo) {

        log.info("profileImage: {}", uploadFile.getOriginalFilename());

        // 파일을 업로드
        String fileUrl = "";
        try {
            fileUrl = uploadService.uploadProfileImage(uploadFile, tokenInfo.getUserId());
            return ResponseEntity.ok().body(fileUrl);
        } catch (IOException e) {
            log.warn("파일 업로드에 실패했습니다.");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /**
     * 로그인한 사용자의 프로필 정보 반환
     *
     * @param tokenInfo 현재 인증된 사용자의 정보
     * @return UserMyPageDto 객체를 담은 ResponseEntity
     */
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal TokenUserInfo tokenInfo) {
        UserMyPageDto userInfo = userMyPageService.getUserInfo(tokenInfo.getUserId());
        log.info("userInfo = {}", userInfo);
        return ResponseEntity.ok(userInfo);
    }

// 유저 정보 수정
    @PutMapping("/userInfo/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                        @RequestBody UserUpdateRequestDto updateDto) {
        try {
            UserMyPageDto updatedUser = userMyPageService.updateUserFields(tokenInfo.getUserId(), updateDto);
            log.info("updatedUser - {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (DuplicateNicknameException ex) {
            // 닉네임 중복 예외 처리
            log.error("닉네임 중복 오류 발생: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 Conflict
        } catch (IllegalArgumentException ex) {
            // 일반적인 예외 처리
            log.error("잘못된 요청: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // 400 Bad Request
        } catch (Exception ex) {
            // 그 외의 예외 처리
            log.error("서버 오류 발생: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Internal Server Error
        }
    }



    // 유저 비밀번호 변경
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                            @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            userMyPageService.changePassword(tokenInfo.getUserId(), changePasswordDto);
            // JSON 형식으로 응답
            return ResponseEntity.ok(Collections.singletonMap("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "예상치 못한 오류가 발생했습니다."));
        }
    }

// 회원탈퇴

    // 이메일 중복확인 API

//    @GetMapping("/check-email")
//    public ResponseEntity<?> checkEmail(String email) {
//        boolean isDuplicate = userMyPageService.checkEmailDuplicate(email);
//        //인증코드메일 발송
//        userMyPageService.sendVerificationEmail(email);
//        return ResponseEntity.ok().body(isDuplicate);
//    }

//    @PostMapping("/check-email")
//    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckDto emailCheckDto) {
//        try {
//            boolean isDuplicate = userMyPageService.checkEmailDuplicate(emailCheckDto.getEmail());
//            System.out.println(isDuplicate);
//            userMyPageService.sendVerificationEmail(emailCheckDto.getEmail());
//
//            Map<String, Boolean> response = new HashMap<>();
//            response.put("isDuplicate", isDuplicate);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            // 로그를 남기거나 사용자에게 오류를 반환
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", true));
//        }
//    }


    @PostMapping("/check-email")
    public ResponseEntity<?> sendEmail(@RequestBody EmailCheckDto emailCheckDto) {
        log.info("email check info - {}", emailCheckDto.getEmail());
        try {
            userMyPageService.sendVerificationEmail(emailCheckDto.getEmail());

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            // 로그를 남기거나 사용자에게 오류를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", true));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> endWithDraw(@RequestBody EmailCheckDto emailCheckDto, @AuthenticationPrincipal TokenUserInfo tokenInfo ) {
        log.info("email 0 info - {}", emailCheckDto.getEmail());
        try {
            userMyPageService.withDrawnUser(emailCheckDto.getEmail(), tokenInfo);

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            // 로그를 남기거나 사용자에게 오류를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", true));
        }
    }

    // 코드 검증
    @PostMapping("/check/code")
    public ResponseEntity<?> verifySendingCode(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                               @RequestBody TemporaryVerficationDto verificationDto) {
        try {
            boolean valid = userMyPageService.verifySendingCode(verificationDto);
            return ResponseEntity.ok("완료.");
        }  catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 실패.");
        }
    }


//    // 코드 검증
//    @PostMapping("/check/code")
//    public ResponseEntity<?> verifySendingCode(@AuthenticationPrincipal TokenUserInfo tokenInfo,
//                                               @RequestBody TemporaryVerficationDto verificationDto) {
//        boolean valid = userMyPageService.verifySendingCode(verificationDto);
//        if(valid) {
//            return ResponseEntity.status(302).body(valid);
//        }
//        return ResponseEntity.status(200).body(valid);
//    }

    // 비밀번호 확인
    @PostMapping("/check/password")
    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                            @RequestBody PasswordVerificationDto verificationDto) {
        log.info("asdadsd - {}", verificationDto.getPassword());
        boolean valid = userMyPageService.verifyPassword(verificationDto);
        if(valid) {
            return ResponseEntity.status(302).body(valid);
        }
        return ResponseEntity.status(200).body(valid);
    }



    /**
     * 매칭 조건이 일치하는 내가 속한 그룹 조회
     * @param tokenInfo - 유저 정보
     * @param matchedGroupRequestDto - 매칭 조건
     * @return 조건이 일치하는 그룹 리스트
     */
    @PostMapping("/mygroup-matched")
    public ResponseEntity<?> getMyGroupsMatched(@AuthenticationPrincipal TokenUserInfo tokenInfo,@RequestBody MatchedGroupRequestDto matchedGroupRequestDto) {
        List<GroupResponseDto> groups = groupQueryService.getMatchedMyGroups(tokenInfo,matchedGroupRequestDto.getId());
        return ResponseEntity.ok().body(groups);
    }

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

    // 비밀번호 확인 엔드포인트
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody CheckPasswordRequestDto dto) {
        boolean isPasswordCorrect = userMyPageService.checkPassword(dto.getEmail(), dto.getPassword());
        System.out.println("isPasswordCorrect = " + isPasswordCorrect);
        if (isPasswordCorrect) {
            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(401).body("{\"success\": false, \"message\": \"비밀번호가 일치하지 않습니다.\"}");
        }
    }

    @PatchMapping("/update-phone")
    public ResponseEntity<?> updatePhoneNumber(@AuthenticationPrincipal TokenUserInfo tokenInfo,
                                               @RequestBody UpdatePhoneNumberDto dto) {
        try {
            userMyPageService.updatePhoneNumber(tokenInfo.getEmail(), dto);
            return ResponseEntity.ok(Map.of("message", "전화번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("전화번호 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("전화번호 업데이트 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "예상치 못한 오류가 발생했습니다."));
        }
    }
}
