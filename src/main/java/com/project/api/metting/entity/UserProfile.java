package com.project.api.metting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * UserProfile
 * : 유저 프로필 엔터티
 * user
 * : 유저 아이디 (1 : 1)
 */

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_profiles")
public class UserProfile {


    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_profile_id")
    private String id; // 프로필 아이디


    @Column(name = "mt_profile_img")
    private String profileImg; // 프로필 이미지 경로

    @Column(name = "mt_profile_introduce")
    private String profileIntroduce; // 프로필 소개


    @JsonIgnore
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id",  nullable = false, unique = true)
    private User user;
}
