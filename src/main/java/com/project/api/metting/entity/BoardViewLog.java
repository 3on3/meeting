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
@Table(name = "mt_board_view_logs")
public class BoardViewLog {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_board_view_log_id")
    private String id; // PK

    @Setter
    @Column(name = "mt_board_view_log_last_viewed_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastViewedAt = LocalDateTime.now(); // 조회 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id", nullable = false)
    private User user; // 유저 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_board_id", nullable = false)
    private Board board; // 보드아이디


}
