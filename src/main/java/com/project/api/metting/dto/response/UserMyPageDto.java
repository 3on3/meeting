package com.project.api.metting.dto.response;

import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.Membership;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMyPageDto {

    private String password; // 비밀번호
    private Date birthDate; // 생년월일 => 나이로 변경
    private String phoneNumber; // 전화번호
    private String univ; // 대학
    private String major; // 전공
    private String nickname; // 닉네임

    private String profileImg; // 프로필 이미지 경로
    private String profileIntroduce; // 프로필 소개
    private Membership membership; // 멤버십 등급

    private int age; // 나이

}
