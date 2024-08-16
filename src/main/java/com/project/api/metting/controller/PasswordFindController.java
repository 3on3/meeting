package com.project.api.metting.controller;

import com.project.api.metting.dto.request.EmailCheckDto;
import com.project.api.metting.dto.request.NewPasswordDto;
import com.project.api.metting.service.PasswordFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("/password")
public class PasswordFindController {

    private final PasswordFindService passwordFindService;

    // 이메일 존재 여부 확인 엔드포인트
    @PostMapping("/email")
    public ResponseEntity<?> checkEmailExists(@RequestBody EmailCheckDto dto) {
        boolean exists = passwordFindService.checkUserExistsByEmail(dto.getEmail());
        if (exists) {
            return ResponseEntity.ok(Map.of("message", "Email exists."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found."));
        }
    }

    // 비밀번호 재설정 엔드포인트
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordDto dto) {
        try {
            // 이메일로 비밀번호 재설정
            passwordFindService.resetPassword(dto.getEmail(), dto);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 재설정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "예상치 못한 오류가 발생했습니다."));
        }
    }
}
