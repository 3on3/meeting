package com.project.api.metting.dto.response;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Place;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class myPageMyChetResponseDto {

    private String Id;

    //그룹 이름
    private String groupName;

    //그룹 성별
    private Gender groupGender;

    //만남 지역
    private Place groupPlace;

    //인원수
    private int memberCount;

    //평균 나이
    private double averageAge;

    //매칭 인원
    private Integer maxNum;

    //학과
    private String Major;

}
