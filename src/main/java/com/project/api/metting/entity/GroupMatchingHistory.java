package com.project.api.metting.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * GroupMatchingHistories
 * : 그룹 매칭 신청/거정/성공 내역이 쌓이는 엔터티
 * requestGroup
 * : 신청자 그룹 엔터티 아이디, setter로 추가
 * responseGroup (FK)
 * : 주최자 그룹 엔터티 아이티(M : 1 - Group)
 */
@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_group_matching_histories")
public class GroupMatchingHistory {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_group_matching_history_id")
    private String id; //  고유 아이디


    @Column(name = "mt_group_matching_history_process")
    @Builder.Default
    private GroupProcess process = GroupProcess.INVITING;


    @Column(name = "mt_group_matching_history_requested_at")
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();


    @Setter
    @Column(name = "mt_group_matching_history_request_group")
    private String requestGroup;


    @ManyToOne
    @JoinColumn(name = "mt_group_id",  nullable = false)
    @Column(name = "mt_group_matching_history_response_group")
    private Group responseGroup;


    @OneToOne(mappedBy = "groupMatchingHistories", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChatRoom chatRooms;


}
