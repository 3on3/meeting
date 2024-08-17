package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequestDto {
    private String type; // "code" 또는 "password"
    private Object verificationDto; // TemporaryVerificationDto 또는 PasswordVerificationDto
}

