package com.project.api.metting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * ChatMessages
 * : 모든 채팅방의 메세지 엔터티
 * chatRooms (FK)
 * : 메세지가 생성된 채팅방(M : 1 - ChatRooms)
 * user (FK)
 * : 메세지를 작성한 유저(M : 1 - User)
 */
@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_chat_messages")
public class ChatMessage {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_chat_message_id")
    private String id; //  고유 아이디


    @Column(name = "mt_chat_message_created_at")
    @Builder.Default // 채팅 시간 기본으로 생성
    private LocalDateTime createdAt = LocalDateTime.now();



    @Column(name = "mt_chat_message_content")
    private String messageContent;


    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mt_chat_room_id",  nullable = false)
    private ChatRoom chatRoom;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mt_user_id",  nullable = false)
    private User user;


}
