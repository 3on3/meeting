package com.project.api.metting.dto.request;

import lombok.Getter;

@Getter
public class TemporaryVerficationDto {

    private String email; // 사용자의 이메일을 저장하는 필드
    private String code;  // 인증 코드를 저장하는 필드

    // 기본 생성자: 인자가 없는 생성자
    public TemporaryVerficationDto() {

    }

    // 모든 필드를 초기화하는 생성자
    public TemporaryVerficationDto(String email, String code) {
        this.email = email; // 전달받은 email 값을 해당 필드에 할당
        this.code = code;   // 전달받은 code 값을 해당 필드에 할당
    }
}
