package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BoardRequestDto {
    String title;
    String content;
}
