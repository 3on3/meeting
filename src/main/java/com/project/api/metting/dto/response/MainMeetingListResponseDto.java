package com.project.api.metting.dto.response;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.Place;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class MainMeetingListResponseDto {

    private String id;

    //그룹 이름
    private String groupName;

    //만남 지역
    private Place groupPlace;

    //그룹 성별
    private Gender groupGender;

    //평균 나이
    private double averageAge;

    //매칭여부
    private Boolean isMatched;

    //매칭 인원
    private Integer maxNum;

    //학과
    private String Major;


//    ,int memberCount 보류
    public MainMeetingListResponseDto(Group group, double averageAge,String hostMajor){
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.groupPlace = group.getGroupPlace();
        this.groupGender = group.getGroupGender();
        this.isMatched = group.getIsMatched();
        this.maxNum = group.getMaxNum();
        this.averageAge = averageAge;
        this.Major = hostMajor;
//        this.memberCount = memberCount;


    }

}
