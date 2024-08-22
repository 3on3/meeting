package com.project.api.metting.dto.request;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertifyCodeRequestDto {

    @Value("${univcert.api.key}")
    private String univCertApiKey;
    private String email;
    private String univName;
    private int code;
}