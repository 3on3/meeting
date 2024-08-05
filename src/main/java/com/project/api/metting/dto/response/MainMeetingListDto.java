package com.project.api.metting.dto.response;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.Place;
import com.project.api.metting.entity.User;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter @ToString
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class MainMeetingListDto {

    private String id;

    //그룹 이름
    private String groupName;

    //만남 지역
    private Place groupPlace;

    //그룹 성별
    private Gender groupGender;

    //평균 나이
    private Integer averageAge;

    //매칭여부
    private Boolean isMatched;

    //매칭 인원
    private Integer maxNum;

    //학과
    private String Major;

    public MainMeetingListDto(Group group, User user){
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.groupPlace = group.getGroupPlace();
        this.groupGender = group.getGroupGender();
        this.isMatched = group.getIsMatched();
        this.maxNum = group.getMaxNum();
        this.Major = user.getMajor();

    }

}
