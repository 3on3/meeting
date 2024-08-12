package com.project.api.metting.dto.request;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveUserDto {

    private String email;      // 사용자 이메일
    private int code;          // 인증 코드
    private String password;   // 비밀번호
    private String action;     // 수행할 작업 (예: "sendCode", "removeUser")


}
