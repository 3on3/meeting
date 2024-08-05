package com.project.api.metting.dto.response;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDto {

    private String groupId;
    private String groupName;
    private String groupPlace;
    private int memberCount;
    private double averageAge;
}
