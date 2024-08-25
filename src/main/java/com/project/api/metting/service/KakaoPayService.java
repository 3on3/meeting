package com.project.api.metting.service;

import com.project.api.metting.dto.response.ApproveResponseDto;
import com.project.api.metting.dto.response.ReadyResponseDto;
import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserMembership;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final UserSignUpService userSignUpService;
    private final UserRepository userRepository;
    private ReadyResponseDto readyResponseDto;
    private String userEmail; // email을 저장할 멤버 변수

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // 보통 username은 email로 설정됨
        }
        return null;
    }

    // 카카오페이 결제창 연결
    public ReadyResponseDto payReady(Map<String, Object> params) {
        String email = (String) params.get("partner_user_id");
        this.userEmail = email;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", "TC0ONETIME");
        parameters.put("partner_order_id", "1234");
        parameters.put("partner_user_id", email); // Use email as partner_user_id
        parameters.put("item_name", (String) params.get("item_name"));
        parameters.put("quantity", "1");
        parameters.put("total_amount", String.valueOf(params.get("total_amount")));
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://gwating.com/payment/approval");
        parameters.put("cancel_url", "http://gwating.com");
        parameters.put("fail_url", "http://gwating.com");
//        parameters.put("approval_url", "http://localhost:8253/payment/approval/");
//        parameters.put("cancel_url", "http://localhost:8253/");
//        parameters.put("fail_url", "http://localhost:8253/");
        System.out.println("parameters = " + parameters);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
        ResponseEntity<ReadyResponseDto> responseEntity = template.postForEntity(url, requestEntity, ReadyResponseDto.class);

        readyResponseDto = responseEntity.getBody();

        return readyResponseDto;
    }

    @Transactional
    public ApproveResponseDto payApprove(String pgToken) {
        if (readyResponseDto == null) {
            throw new IllegalStateException("결제 준비 응답이 null입니다.");
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", "TC0ONETIME");
        parameters.put("tid", readyResponseDto.getTid()); // Transaction ID from ready response
        parameters.put("partner_order_id", "1234");
        parameters.put("partner_user_id", this.userEmail); // Use partner_user_id from ready response
        parameters.put("pg_token", pgToken); // Payment token

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
        ResponseEntity<ApproveResponseDto> responseEntity = template.postForEntity(url, requestEntity, ApproveResponseDto.class);

        ApproveResponseDto approveResponse = responseEntity.getBody();

        return approveResponse;
    }

    // 카카오페이 측에 요청 시 헤더부에 필요한 값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY DEVB9EAF6F60123BBF8BE05C0E3CB3C019D16517");
        headers.set("Content-Type", "application/json");

        return headers;
    }

    // 결제 승인 후 사용자 멤버십 업데이트
    public void updateUserMembershipToPremium(String email) {
        updateMembershipToPremium(email);
    }

    @Transactional
    public void updateMembershipToPremium(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        UserMembership userMembership = user.getMembership();
        System.out.println("userMembership은??  " + userMembership);
        if (userMembership == null) {
            throw new IllegalStateException("UserMembership not found for email: " + email);
        }

        userMembership.setAuth(Membership.PREMIUM);
        userRepository.save(user);
        log.info("User with email {} has been upgraded to PREMIUM membership.", email);
    }
}
