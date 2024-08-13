package com.project.api.metting.dto.response;

import com.project.api.metting.entity.Gender;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    // 인증정보(이메일, 권한정보, 토큰정보)를 클라이언트에게 전송
    private String email; // 이메일
    private String auth; // 권한 정보 (COMMON, ADMIN)
    private String token; // 인증 토큰
    private String refreshToken; // 리프레쉬 토큰 (자동로그인 확인시 필요)
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String univName;
    private String major; // 전공
    private Gender gender; // 성별
    private String nickname; // 성별
    private String profileImg; // 프로필 이미지
    private boolean isWithdrawn; // 탈퇴 여부
}
