package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.LoginRequestDto;
import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.dto.response.LoginResponseDto;
import com.project.api.metting.service.UserSignInService;
import com.project.api.metting.service.UserSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class RegisterController {


    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;



    //이메일 중복확인 API
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        log.info("님이 보낸 이메일 {}", email);
        boolean b = userSignUpService.checkEmailDuplicate(email);
        return ResponseEntity.ok().body(b);
    }

    //인증 코드 검증 API
    @GetMapping("/code")
    public ResponseEntity<?> verifyCode(String email, String code) {
        log.info("{}'s verify code is [ {} ]", email, code);
        boolean isMatch = userSignUpService.isMatchCode(email, code);
        return ResponseEntity.ok().body(isMatch);
    }

    //회원가입 마무리 처리
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRegisterDto dto) {

        log.info("save user info - {}", dto);
        try {
            userSignUpService.confirmSignUp(dto);
        } catch (LoginFailException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("saved success");
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
}
