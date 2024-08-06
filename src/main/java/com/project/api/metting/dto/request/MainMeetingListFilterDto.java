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

    private Gender gender; // 성별
    private Place groupPlace; // 만남지역
    private Integer maxNum; // 최대 인원 수

}
