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
    private String chatRoomName;
    private String major;
    private Gender gender;
    private Integer groupMemberCount;
    private Place groupPlace;
    @Setter
    private Integer age;
    private Integer chatMemberCount;
}
