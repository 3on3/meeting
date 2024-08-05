package com.project.api.metting.dto.response;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMatchingResponseDto {
    // 주최자 그룹
    private String groupId;
}
