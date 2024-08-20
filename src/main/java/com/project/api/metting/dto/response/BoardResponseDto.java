package com.project.api.metting.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class BoardResponseDto {
    private String id;
    private String title;
    private String content;
    private String writer;
    private String createdAt;
    private Integer viewCount;
}
