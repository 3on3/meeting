package com.project.api.metting.dto.request;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Place;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCreateDto {
    private String groupName; // 그룹 이름
    private Place groupPlace; // 만남지역
    private Integer maxNum; // 최대 인원 수
    private Gender groupGender; // 그룹의 성별

}
