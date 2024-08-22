package com.project.api.metting.controller;

import com.project.api.metting.dto.request.OrderCreateFormDto;
import com.project.api.metting.dto.response.ApproveResponseDto;
import com.project.api.metting.dto.response.ReadyResponseDto;
import com.project.api.metting.service.KakaoPayService;
import com.project.api.metting.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public ResponseEntity<?> payReady(@RequestBody OrderCreateFormDto dto) {
        try {
            log.info("주문 상품 이름: {}", dto.getName());
            log.info("주문 금액: {}", dto.getTotalPrice());

            // 카카오 결제 준비 작업 수행
            ReadyResponseDto readyResponse = kakaoPayService.payReady(dto.getName(), dto.getTotalPrice());

            // 세션에 결제 고유번호(tid) 저장
            SessionUtil.addAttribute("tid", readyResponse.getTid());
            log.info("결제 고유번호: {}", readyResponse.getTid());

            // 성공 시 ReadyResponseDto와 함께 OK 응답 반환
            return ResponseEntity.ok(readyResponse);
        } catch (Exception e) {
            log.error("결제 준비 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 준비 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> payCompleted(@RequestBody Map<String, String> requestBody) {
        try {
            String pgToken = requestBody.get("pg_token");
            String tid = SessionUtil.getStringAttributeValue("tid");

            if (tid == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 결제 고유번호입니다.");
            }

            log.info("결제승인 요청을 인증하는 토큰: {}", pgToken);
            log.info("결제 고유번호: {}", tid);

            // 카카오 결제 승인 작업 수행
            ApproveResponseDto approveResponse = kakaoPayService.payApprove(tid, pgToken);

            // 성공 시 ApproveResponseDto와 함께 OK 응답 반환
            return ResponseEntity.ok(approveResponse);
        } catch (Exception e) {
            log.error("결제 승인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 승인 중 오류가 발생했습니다.");
        }
    }
}
