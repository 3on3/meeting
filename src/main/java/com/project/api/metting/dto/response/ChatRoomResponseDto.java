package com.project.api.metting.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private String name;
    private String id;
    private String historyID;
}
