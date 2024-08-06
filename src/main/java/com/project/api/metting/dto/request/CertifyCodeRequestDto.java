package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertifyCodeRequestDto {
    private String email;
    private String univName;
    private int code;
}