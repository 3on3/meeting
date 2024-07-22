package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_group_users")
public class GroupUsers {


    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_group_user_id")
    private String id; //  고유 아이디



    @Column(name = "mt_group_joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "mt_group_user_auth")
    @Builder.Default // 그룹 생성 시간
    private GroupAuth auth = GroupAuth.MEMBER;


    @Column(name = "mt_group_user_status")
    @Builder.Default // 그룹 생성 시간
    private GroupStatus status = GroupStatus.INVITING;

}
