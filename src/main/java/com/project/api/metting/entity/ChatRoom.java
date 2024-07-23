package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ChatRooms
 * : 그룹 대 그룹으로 매칭된 후 생성되는 채팅방 엔터티
 * groupMatchingHistories (FK)
 * : 그룹 대 그룹으로 매칭된 후 생성되는 매칭 내역(1 : 1)
 * chatMessages (FK) (excludes)
 * : 채팅방에서 생성되는 모든 메세지(1 : M - ChatMessages)
 */

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_chat_rooms")
public class ChatRoom {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_chat_room_id")
    private String id; //  고유 아이디


    @Column(name = "mt_chat_room_name")
    private String chatRoomName;


    @Column(name = "mt_chat_room_created_at")
    @Builder.Default // 가입시간 기본으로 생성
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(name = "mt_chat_room_meeting_date")
    private LocalDateTime meetingDate;


    @Column(name = "mt_chat_room_is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_group_matching_history_id",  nullable = false)
    private GroupMatchingHistory groupMatchingHistories;

    @OneToMany(mappedBy = "chatRooms", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages;
}
