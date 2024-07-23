package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id",  nullable = false)
    private User user;
}
