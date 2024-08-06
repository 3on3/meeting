package com.project.api.metting.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private String roomId;
    private String message;
}
