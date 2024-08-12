package com.project.api.metting.dto.response;


import com.project.api.metting.entity.User;
import lombok.*;

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
    private String name;
    private String id;
    private String Auth;



    public UserResponseDto(User user) {
        this.univName = user.getUnivName();
        this.major = user.getMajor();
        this.name = user.getName();
        this.id = user.getId();
        this.Auth = user.getAuth().toString();
    }
}
