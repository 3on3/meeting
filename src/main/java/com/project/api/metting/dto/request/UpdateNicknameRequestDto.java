package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateNicknameRequestDto {

    private String email; // 이메일
    private String nickname; // 닉네임
}
