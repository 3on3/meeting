package com.project.api.metting.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmListRequestDto {

    private String alarmId;
    private String requestGroupName;
    private String requestGroupHostProfile;
    private String responseGroupId;
    private LocalDateTime requestedAt;

}
