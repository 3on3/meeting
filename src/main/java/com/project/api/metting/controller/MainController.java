package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainService mainService;

    /**
     * 미팅 리스트를 조
     *
     * @param tokenUserInfo 인증된 사용자의 정보
     * @param pageNo 요청한 페이지 번호
     * @param gender 필터링할 성별 (선택 사항)
     * @param region 필터링할 지역 (선택 사항)
     * @param personnel 필터링할 인원수 (선택 사항)
     * @return 미팅 리스트 페이지
     */
    @GetMapping("/main")
    public ResponseEntity<?> getMeetingList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                            @RequestParam int pageNo,
                                            @RequestParam (required = false)String gender,
                                            @RequestParam (required = false) String region,
                                            @RequestParam (required = false) Integer personnel
                                            ) {

        Page<MainMeetingListResponseDto> meetingList = null;
        try {
            meetingList = mainService.getMeetingList(tokenUserInfo.getEmail(), pageNo,gender,region,personnel);
        } catch (Exception e) {
            log.error("Error occurred while fetching meeting list", e);
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(meetingList);
    }

}
