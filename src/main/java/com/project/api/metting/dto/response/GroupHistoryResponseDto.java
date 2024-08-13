package com.project.api.metting.dto.response;

import com.project.api.metting.entity.GroupMatchingHistory;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class GroupHistoryResponseDto {
    private String responseId;

    public GroupHistoryResponseDto (GroupMatchingHistory GroupMatchingHistory) {
        this.responseId = GroupMatchingHistory.getResponseGroup().getId();
    }
}
