package com.project.api.metting.dto.response;


import com.project.api.metting.entity.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private int averageAge;

    //매칭여부
    private Boolean isMatched;

    //매칭 인원
    private Integer maxNum;

    //학과
    private String Major;



    // 로그인한 유저 기준 해당 그룹과 히스토리가 있는지
    @Setter
    @Builder.Default
    private MatchingStatus matchingStatus = MatchingStatus.NONE;



//    ,int memberCount 보류
    public MainMeetingListResponseDto(Group group, int averageAge,String hostMajor){
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.groupPlace = group.getGroupPlace();
        this.groupGender = group.getGroupGender();
        this.isMatched = group.getIsMatched();
        this.maxNum = group.getMaxNum();
        this.averageAge = averageAge;
        this.Major = hostMajor;
//
    }


}
