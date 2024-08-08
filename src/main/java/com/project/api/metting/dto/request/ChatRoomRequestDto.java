package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequestDto {

    private String requestGroupId;
    private String responseGroupId;
}
