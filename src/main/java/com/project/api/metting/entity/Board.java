package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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
    @Builder.Default
    private LocalDateTime modifiedAt = null;

    @Setter
    @Column(name = "mt_board_is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부

    @Setter
    @Column(name = "mt_board_view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0; // 조회수

    @Column(name = "mt_board_profile_img_url")
    private String profileImgFile; // 랜덤 이미지

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


    @PrePersist
    public void prePersist() {
        if (this.profileImgFile == null) {
            this.profileImgFile = getRandomImageFile(); // 랜덤 이미지 선택
        }
    }

    // 랜덤 이미지 URL 선택 로직
    private String getRandomImageFile() {
        String[] images = {
                "developer-hun2zz.png",
                "developer-jin.png",
                "developer-jinu.png",
                "developer-mimi.png",
                "developer-silverji.png",
                "developer-yocong.png"
        };
        Random random = new Random();
        int index = random.nextInt(images.length);
        return images[index];
    }

}
