package com.project.api.metting.dto.request;

import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Place;
import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChatListRequestDto {

    private String chatRoomId;
    private String groupName;
    private String major;
    private Gender gender;
    private Integer maxNum;
    private Place groupPlace;
    @Setter
    private int age;
}
