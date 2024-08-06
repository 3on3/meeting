package com.project.api.metting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * GroupUsers
 * : 그룹에 속한 모든 유저 컬럼
 * user (FK)
 * : 컬럼의 유저 아이디 (M : 1 - User)
 * group (FK)
 * : 컬럼의 그룹 아이디 (M : 1 - Group)
 */
@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_group_users")
public class GroupUser {


    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_group_user_id")
    private String id; //  고유 아이디



    @Column(name = "mt_group_joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "mt_group_user_auth")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private GroupAuth auth = GroupAuth.MEMBER;


    @Column(name = "mt_group_user_status")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Setter
    private GroupStatus status = GroupStatus.INVITING;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id",  nullable = false)
    private User user;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @Setter
    @JoinColumn(name = "mt_group_id",  nullable = false)
    private Group group;


}
