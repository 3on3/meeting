package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AlarmRequestDto {

    @Setter
    private String email;
}
