package com.project.api.metting.dto.request;

import com.project.api.metting.entity.ChatMessage;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ChatMessageRequestDto {
    private String userId;
    private String userEmail;
    private String userNickname;
    private String messageId;
    private String messageContent;


     public ChatMessageRequestDto(ChatMessage chatMessage) {
         this.userEmail = chatMessage.getUser().getEmail();
         this.userId = chatMessage.getUser().getId();
         this.userNickname = chatMessage.getUser().getNickname();
         this.messageId = chatMessage.getId();
         this.messageContent = chatMessage.getMessageContent();
     }
}
