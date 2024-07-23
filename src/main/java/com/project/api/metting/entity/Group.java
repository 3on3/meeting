package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Group
 * : 그룹 엔터티
 * groupUsers (excludes)
 * : 그룹에 속한 유저 리스트 (1 : M - GroupUsers)
 * groupMatchingHistories (excludes)
 * : 그룹에 대해 생성된 매칭 신청/거절/성공 내역 (1 : M - GroupMatchingHistories)
 */

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_groups")
public class Group {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_group_id")
    private String id; //  고유 아이디


    @Column(name = "mt_group_name")
    private String groupName; // 그룹 이름

    @Column(name = "mt_group_place")
    private Place groupPlace; // 만남지역

    @Column(name = "mt_group_inviting_code", unique = true)
    private String code; // 참여 코드

    @Column(name = "mt_group_max_number")
    private Integer maxNum; // 최대 인원 수

    @Column(name = "mt_group_created_at")
    @Builder.Default // 그룹 생성 시간
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "mt_group_is_matched")
    @Builder.Default
    private Boolean isMatched = false; // 매칭 여부


    @Column(name = "mt_group_is_deleted")
    @Builder.Default
    private Boolean isDeleted = false; // 매칭 여부

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupUser> groupUsers;




    @OneToMany(mappedBy = "responseGroup", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMatchingHistory> groupMatchingHistories;


}
