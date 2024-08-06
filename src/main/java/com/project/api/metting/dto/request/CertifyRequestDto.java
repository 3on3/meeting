package com.project.api.metting.dto.request;

import com.univcert.api.UnivCert;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertifyRequestDto {

    private String email;
    private String univName;
    private boolean univ_check;
}
