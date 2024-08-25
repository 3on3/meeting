package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * BoardReply
 * : 게시글에 대한 댓글 정보를 담은 엔터티
 * author, boardId
 * : 해당 댓글의 작성자, 보드 아이디 (M : 1 - User, Board)
 */
@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_board_replies")
public class BoardReply {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_board_reply_id")
    private String id; // PK

    @Column(name = "mt_board_reply_content" , nullable = false)
    private String content; // 내용

    @Column(name = "mt_board_reply_created_at" , nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 일자

    @Setter
    @Column(name = "mt_board_reply_is_deleted" , nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부

    @Column(name = "mt_board_profile_img_url")
    private String profileImgFile; // 랜덤 이미지

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id")
    private User author; // 작성자



    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_board_id" , nullable = false)
    private Board board; // 보드


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
