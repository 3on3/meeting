package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "mt_alarms")
public class Alarm {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_chat_message_id")
    private String id; //  고유 아이디

    @Column(name = "mt_alarm_status")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Setter
    private AlarmStatus status = AlarmStatus.NOT_CHECKED; // 참여 코드

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "mt_group_matching_history_id",  nullable = false)
    private GroupMatchingHistory groupMatchingHistory;
}
