package com.project.api.metting.dto.response;


import com.project.api.metting.entity.BoardReply;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class BoardRepliesResponseDto {
    private String id;//댓글고유 id
    private String content;//내용
    private LocalDate createdDate;//작성일자


}
