package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = "user")
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_user_membership")
public class UserMembership {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_profile_id")
    private String id; // 멤버쉽 고유 아이디


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "mt_user_membership_auth", nullable = false)
    private Membership auth = Membership.GENERAL;



    @Column(name = "mt_user_membership_registered_at")
    @Builder.Default // 가입시간 기본으로 생성
    private LocalDateTime registeredAt = LocalDateTime.now();


    @OneToOne
    @JoinColumn(name = "mt_user_id",  nullable = false, unique = true)
    private User user;


}
