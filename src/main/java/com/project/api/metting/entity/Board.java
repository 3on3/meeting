package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Board
 * : 게시글 정보를 담은 엔터티
 * author
 * : 해당 게시글의 작성자(M : 1 - User)
 */
@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_boards")
public class Board {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_board_id")
    private String id; // PK

    @Setter
    @Column(name = "mt_board_title", nullable = false)
    private String title; // 제목

    @Setter
    @Column(name = "mt_board_content", nullable = false)
    private String content; // 내용



    @Column(name = "mt_board_created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 일자

    @Setter
    @Column(name = "mt_modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "mt_board_is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부

    @Setter
    @Column(name = "mt_board_view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0; // 조회수

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id", nullable = false)
    private User author; // 작성자

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardReply> boardReplies;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardViewLog> boardViewLogs;


}
