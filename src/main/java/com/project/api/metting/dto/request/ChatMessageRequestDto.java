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
    @Builder.Default
    private String userId = null;
    @Builder.Default
    private String userEmail = null;
    @Builder.Default
    private String userNickname = null;
    @Builder.Default
    private String messageId = null;
    @Builder.Default
    private String messageContent = null;
    @Builder.Default
    private String profileImg = null;
    @Builder.Default
    private LocalDateTime messageAt = null;


     public ChatMessageRequestDto(ChatMessage chatMessage) {
         this.userEmail = chatMessage.getUser().getEmail();
         this.userId = chatMessage.getUser().getId();
         this.userNickname = chatMessage.getUser().getNickname();
         this.messageId = chatMessage.getId();
         this.messageContent = chatMessage.getMessageContent();
         this.profileImg = chatMessage.getUser().getUserProfile().getProfileImg();
         this.messageAt = chatMessage.getCreatedAt();
     }
}
