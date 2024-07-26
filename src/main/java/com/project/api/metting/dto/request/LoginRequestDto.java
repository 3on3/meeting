package com.project.api.metting.dto.request;

import lombok.*;

// 로그인시 클라이언트에서 서버로 보낼 정보
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email; // 이메일
    private String password; // 비밀번호
    private boolean autoLogin; // 자동로그인 여부
}
