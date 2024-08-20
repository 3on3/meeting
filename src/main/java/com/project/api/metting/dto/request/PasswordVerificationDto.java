package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordVerificationDto {

    private String email;    // 사용자의 이메일을 저장하는 필드
    private String password; // 사용자의 비밀번호를 저장하는 필드


//    // 기본 생성자: 인자가 없는 생성자
//    public PasswordVerificationDto() {
//
//    }

    // 모든 필드를 초기화하는 생성자
//    public PasswordVerificationDto(String email, String password) {
//        this.email = email;       // 전달받은 email 값을 해당 필드에 할당
//        this.password = password; // 전달받은 password 값을 해당 필드에 할당
//    }
}