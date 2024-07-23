package com.project.api.metting.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_group_matching_histories")
public class GroupMatchingHistories {
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







    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_group_matching_history_response_group",  nullable = false, referencedColumnName = "mt_group_id")
    private Group responseGroup;


    @OneToOne(mappedBy = "groupMatchingHistories", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChatRooms chatRooms;


}
