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
@Table(name = "mt_chat_messages")
public class ChatMessages {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_chat_message_id")
    private String id; //  고유 아이디


    @Column(name = "mt_chat_message_created_at")
    @Builder.Default // 채팅 시간 기본으로 생성
    private LocalDateTime createdAt = LocalDateTime.now();



    @Column(name = "mt_chat_message_context")
    private String messageContext;

}
