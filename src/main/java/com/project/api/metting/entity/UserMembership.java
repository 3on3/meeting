package com.project.api.metting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * UserMembership
 * : 유저 멤버십 엔터티
 * user (FK)
 * : 유저 아이디 (1 : 1)
 */
@Getter
@ToString()
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
    @Column(name = "mt_user_membership_id")
    private String id; // 멤버쉽 고유 아이디


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "mt_user_membership_auth", nullable = false)
    private Membership auth = Membership.GENERAL;



    @Column(name = "mt_user_membership_registered_at")
    @Builder.Default // 가입시간 기본으로 생성
    private LocalDateTime registeredAt = LocalDateTime.now();

    @JsonIgnore
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "mt_user_id",  nullable = false, unique = true)
    private User user;


}
