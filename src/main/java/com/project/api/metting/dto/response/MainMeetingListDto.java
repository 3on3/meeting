package com.project.api.metting.dto.response;


import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.Place;
import com.project.api.metting.entity.User;
import lombok.*;

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
    private String groupPlace;

    //그룹 성별
    private String groupGender;

    //매칭여부
    private Boolean isMatched;

    //매칭 인원
    private Integer maxNum;

    //학과
    private String Major;

    public MainMeetingListDto(Group group, User user){
        this.id = group.getId().toString();
        this.groupName = group.getGroupName();
        this.groupPlace = String.valueOf(group.getGroupPlace());
        this.groupGender = String.valueOf(group.getGroupGender());
        this.isMatched = group.getIsMatched();
        this.maxNum = group.getMaxNum();
        this.Major = user.getMajor();




    }

}
