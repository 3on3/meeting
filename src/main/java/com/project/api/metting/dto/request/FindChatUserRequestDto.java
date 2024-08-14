package com.project.api.metting.dto.request;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class FindChatUserRequestDto {

   private List<ChatUserRequestDto> responseChatUser;
   private List<ChatUserRequestDto> requestChatUser;
   private String responseGroupName;
   private String requestGroupName;
   private String responseHostUserId;
   private String requestHostUserId;
}
