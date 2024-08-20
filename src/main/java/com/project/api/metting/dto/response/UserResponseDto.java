package com.project.api.metting.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.api.metting.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String univName;
    private String major;
    private String profileImageUrl;
    private String nickname;
    private String name;
    private String id;
    private String Auth;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime joinedAt;




    public UserResponseDto(User user, LocalDateTime joinedAt, String profileImageUrl) {
        this.univName = user.getUnivName();
        this.major = user.getMajor();
        this.name = user.getName();
        this.id = user.getId();
        this.Auth = user.getAuth().toString();
        this.nickname = user.getNickname();
        this.joinedAt = joinedAt;
        this.profileImageUrl = profileImageUrl;
    }
}
