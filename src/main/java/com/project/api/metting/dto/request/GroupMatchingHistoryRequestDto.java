package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMatchingHistoryRequestDto {
    // 신청자 그룹
    private String requestGroupId;
    // 주최자 그룹
    private String responseGroupId;
}
