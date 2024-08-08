package com.project.api.metting.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMatchingResponseDto {
    private String responseGroupId;
    private String requestGroupId;
}
