package com.project.api.metting.controller;

import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.LoginResponseDto;
import com.project.api.metting.service.UserSignInService;
import com.project.api.metting.service.UserSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class RegisterController {

    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;

    // 이메일 중복확인 및 인증 메일 발송 API
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody CertifyRequestDto dto) {
        log.info("Email verification request for: {}", dto.getEmail());
        boolean isDuplicate = userSignUpService.checkEmailDuplicate(dto.getEmail(), dto.getUnivName());
        return ResponseEntity.ok().body(isDuplicate);

    }

    // 인증 코드 검증 API
    @PostMapping("/code")
    public ResponseEntity<?> verifyCode(@RequestBody CertifyCodeRequestDto dto) {
        log.info("{}'s verify code is [ {} ]", dto.getEmail(), dto.getCode());
        boolean isMatch = userSignUpService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok().body(isMatch);
    }

    // 회원가입 마무리 처리
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRegisterDto dto) {
        log.info("Save user info - {}", dto);
        try {
            userSignUpService.confirmSignUp(dto);
        } catch (LoginFailException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Saved success");
    }

    //로그인 처리
    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody LoginRequestDto dto) {
        try {
            // 사용자가 회원가입시 입력한 정보(LoginRequestDto)로 회원 로그인 인증
            // 로그인 정보가 맞다면 토큰 생성해서 LoginResponseDto에 성공 정보를 담아 반환
            LoginResponseDto responseDto = userSignInService.authenticate(dto, dto.isAutoLogin());
            return ResponseEntity.ok().body(responseDto);
        } catch (LoginFailException e) {
            // service에서 예외발생 (로그인 실패)
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }

    // 새로운 액세스 토큰을 발급
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String email, @RequestParam String refreshToken) {
        try {
            LoginResponseDto responseDto = userSignInService.refreshAccessToken(email, refreshToken);
            return ResponseEntity.ok().body(responseDto);
        } catch (LoginFailException e) {
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }

    // 닉네임 업데이트 처리
    @PostMapping("/update-nickname")
    public ResponseEntity<?> updateNickname(@RequestBody UpdateNicknameRequestDto dto) {
        try {
            userSignUpService.updateNickname(dto.getEmail(), dto.getNickname());
            return ResponseEntity.ok().body("Nickname updated successfully");
        } catch (Exception e) {
            log.error("Error during nickname update", e);
            return ResponseEntity.status(500).body("중복된 닉네임 입니다!");
        }
    }

    // 닉네임 중복 확인 API
    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestBody String nickname) {
        boolean isDuplicate = userSignUpService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate); // boolean 값 반환
    }

    // 전화번호 중복 확인 API
    @GetMapping("/check-phone-number")
    public ResponseEntity<?> checkPhoneNumber(@RequestParam String phoneNumber) {
        log.info("Checking phone number: {}", phoneNumber);
        boolean isDuplicate = userSignUpService.checkPhoneNumberDuplicate(phoneNumber);
        return ResponseEntity.ok(isDuplicate); // boolean 값 반환
    }
}
