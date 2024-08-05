package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * GroupMatchingHistories
 * : 그룹 매칭 신청/거정/성공 내역이 쌓이는 엔터티
 * requestGroup
 * : 신청자 그룹 엔터티의 고유아이디, setter로 기입
 * responseGroup (FK)
 * : 주최자 그룹 엔터티의 고유아이디 (M : 1 - Group)
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
    @Setter
    private GroupProcess process = GroupProcess.INVITING;


    @Column(name = "mt_group_matching_history_requested_at")
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();



    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_group_matching_history_request_group",  nullable = false, referencedColumnName = "mt_group_id")
    private Group requestGroup;


    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_group_matching_history_response_group",  nullable = false, referencedColumnName = "mt_group_id")
    private Group responseGroup;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "groupMatchingHistory", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;


}
