package com.project.api.metting.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    //    @JsonProperty("product_img")
    private String profileImg; // 프로필 이미지 경로

    //    @JsonProperty("profile_introduce")
    private String profileIntroduce; // 프로필 소개

    private String nickname; // 닉네임

    private String membership; // 멤버십 등급

    private String univ; // 대학

    private String major; // 전공

    private String profileImage;


}
