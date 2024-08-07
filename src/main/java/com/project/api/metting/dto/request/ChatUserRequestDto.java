package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ChatUserRequestDto {
    private String imgUrl;
    private String userNickname;
    private String univ;
    private String major;
}
