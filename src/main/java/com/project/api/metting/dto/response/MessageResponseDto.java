package com.project.api.metting.dto.response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class MessageResponseDto {

    private String id;
    private String userName;
    private String auth;
    private String content;

}
