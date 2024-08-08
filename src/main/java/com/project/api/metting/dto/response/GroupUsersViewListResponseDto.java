package com.project.api.metting.dto.response;


import com.project.api.metting.entity.GroupAuth;
import com.project.api.metting.entity.Place;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class GroupUsersViewListResponseDto {
    private List<UserResponseDto> users;
    private int averageAge;
    private String meetingPlace;
    private int totalMembers;
    private String gender;
    private String groupName;
    private String groupAuth;
    private String inviteCode;

    public GroupUsersViewListResponseDto(List<UserResponseDto> users, int averageAge, String meetingPlace, int totalMembers,
                                         String gender, String groupName, String groupAuth, String groupInviteCode) {
        this.users = users;
        this.averageAge = averageAge;
        this.meetingPlace = meetingPlace;
        this.totalMembers = totalMembers;
        this.gender = gender;
        this.groupName = groupName;
        this.groupAuth = groupAuth;
        this.inviteCode = groupInviteCode;
    }

}
