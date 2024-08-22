package com.project.api.metting.dto.request;


import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class BoardRepliesRequestDto {

    String content;//작성 글

    String boardId;//게시글 ID


}
