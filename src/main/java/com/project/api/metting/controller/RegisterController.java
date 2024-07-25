package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.UserRegisterDto;
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


}
