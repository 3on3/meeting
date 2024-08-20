package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePhoneNumberDto {
    private String phoneNumber; // 전화번호
}