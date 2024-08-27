package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateFormDto {

    private String name;
    private int totalPrice;
    private String email; // 이메일 필드 추가
}
