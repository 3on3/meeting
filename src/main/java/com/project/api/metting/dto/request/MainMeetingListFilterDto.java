package com.project.api.metting.dto.request;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Place;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class MainMeetingListFilterDto {

    private String gender; // 성별
    private String groupPlace; // 만남지역
    private Integer maxNum; // 최대 인원 수
    private Boolean isMatched; //매칭 유무
    private Integer pageNo; // 페이지 번호
    private Integer pageSize;//몇개의 페이지를 보여줄건지


}
