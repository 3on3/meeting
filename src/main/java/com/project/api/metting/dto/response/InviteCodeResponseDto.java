package com.project.api.metting.dto.response;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCodeResponseDto {

    private String inviteLink;
    private long remainingTime; // 초 단위 남은 시간
}
