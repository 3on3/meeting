package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDeletRequestDto {
    String replyId; //댓글 아이디
}
