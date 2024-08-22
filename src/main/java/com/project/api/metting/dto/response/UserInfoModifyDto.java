package com.project.api.metting.dto.response;

import com.project.api.metting.entity.Gender;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoModifyDto {

    private String name; // 이름
    private String email; // 이메일
    private Date birthDate; // 생년월일
    private Gender gender; // 성별
}
