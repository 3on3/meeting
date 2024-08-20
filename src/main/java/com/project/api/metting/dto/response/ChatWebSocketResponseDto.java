package com.project.api.metting.dto.response;


import com.project.api.metting.dto.request.ChatMessageRequestDto;
import com.project.api.metting.entity.ChatMessage;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ChatWebSocketResponseDto {
    private String type;
    private ChatMessageRequestDto message;
    private String chatroomId;
}
