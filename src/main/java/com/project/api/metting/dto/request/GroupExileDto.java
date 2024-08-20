package com.project.api.metting.dto.request;


import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupExileDto {
    private String groupId;
    private String userId;
}
