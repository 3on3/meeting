package com.project.api.metting.dto.response;


import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.Place;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDto {

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

    //학과
    private String Major;

    public GroupResponseDto(Group group,int memberCount, int averageAge, String Major){
        this.Id = group.getId();
        this.groupName = group.getGroupName();
        this.groupGender = group.getGroupGender();
        this.groupPlace = group.getGroupPlace();
        this.memberCount = memberCount;
        this.averageAge = averageAge;
        this.Major = Major;

    }
}
