package com.project.api.metting.dto.response;

import com.project.api.metting.entity.User;
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
    @Setter
    private String modifiedAt = null;
    private Integer viewCount;
    @Setter
    private Boolean isAuthor = false;
}
