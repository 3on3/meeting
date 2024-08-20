package com.project.api.metting.dto.response;

import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMyPageDto {


    private String profileImg; // 프로필 이미지 경로
    private String profileIntroduce; // 프로필 소개
    private String nickname; // 닉네임
    private int age; // 나이
    private Membership membership; //멤버십
    private String univ; // 대학
    private String major; // 전공
    private String currentPassword; //현재 비밀번호

    private String newPassword; //새로운 비밀번호

    private String confirmNewPassword; //새로운 비밀번호 확인


}
