package com.project.api.metting.dto.response;


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

    public GroupUsersViewListResponseDto(List<UserResponseDto> users, int averageAge, String meetingPlace, int totalMembers, String gender) {
        this.users = users;
        this.averageAge = averageAge;
        this.meetingPlace = meetingPlace;
        this.totalMembers = totalMembers;
        this.gender = gender;
    }

}
