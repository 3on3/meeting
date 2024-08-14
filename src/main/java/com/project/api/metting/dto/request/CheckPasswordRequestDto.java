package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPasswordRequestDto {

    private String email;
    private String password;
}
