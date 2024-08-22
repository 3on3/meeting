package com.project.api.metting.controller;

import com.project.api.metting.dto.request.OrderCreateFormDto;
import com.project.api.metting.dto.response.ApproveResponseDto;
import com.project.api.metting.dto.response.ReadyResponseDto;
import com.project.api.metting.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/payment")
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public ResponseEntity<ReadyResponseDto> payReady(@RequestBody Map<String, Object> params) {
        try {
            // 카카오 결제 준비 작업 수행
            ReadyResponseDto readyResponse = kakaoPayService.payReady(params);
            log.info("결제 준비 응답: {}", readyResponse);

            return ResponseEntity.ok(readyResponse);
        } catch (Exception e) {
            log.error("결제 준비 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> payCompleted(@RequestBody Map<String, String> requestBody) {
        try {
            String pgToken = requestBody.get("pg_token");

            // Get email from ApproveResponseDto
            ApproveResponseDto approveResponse = kakaoPayService.payApprove(pgToken);
            String email = approveResponse.getPartner_user_id();

            if (pgToken == null) {
                log.error("pgToken 값이 null입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 인증 토큰이 제공되지 않았습니다.");
            }

            if (email == null) {
                log.error("이메일이 null입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일이 제공되지 않았습니다.");
            }

            // 결제 승인 작업 시작
            log.info("카카오페이 결제 승인 응답: {}", approveResponse);

            if (approveResponse != null && approveResponse.getTid() != null) {
                log.info("결제가 승인되었습니다. 사용자의 멤버십을 업데이트합니다.");
                kakaoPayService.updateUserMembershipToPremium(email);
                return ResponseEntity.ok(approveResponse);
            } else {
                log.error("결제 승인에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 승인에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("결제 승인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 승인 중 오류가 발생했습니다.");
        }
    }
}
