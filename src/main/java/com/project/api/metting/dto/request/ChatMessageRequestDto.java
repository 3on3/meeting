package com.project.api.metting.dto.request;

import com.project.api.metting.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ChatMessageRequestDto {
    private String userId = null;
    private String userEmail = null;
    private String userNickname =null;
    private String messageId = null;
    private String messageContent = null;
    private String profileImg = null;
    private LocalDateTime messageAt = null;
    @Setter
    private boolean isDelete;


     public ChatMessageRequestDto(ChatMessage chatMessage) {
         this.userEmail = chatMessage.getUser().getEmail();
         this.userId = chatMessage.getUser().getId();
         this.userNickname = chatMessage.getUser().getNickname();
         this.messageId = chatMessage.getId();
         this.messageContent = chatMessage.getMessageContent();
         this.profileImg = chatMessage.getUser().getUserProfile().getProfileImg();
         this.messageAt = chatMessage.getCreatedAt();
         this.isDelete = false;
     }
}
