package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewPasswordDto {

    private String email;              // 사용자 이메일
    private String newPassword;        // 새 비밀번호
    private String confirmNewPassword; // 새 비밀번호 확인
}
