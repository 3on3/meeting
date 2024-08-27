package com.project.api.metting.dto.request;

import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDto {
    // 주최자 그룹
    private Group responseGroup;
    // 신청자 그룹
//    private Group requestedGroup;
    // 해당 매칭 신청 히스토리
    private GroupMatchingHistory history;
}
