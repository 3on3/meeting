package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessagesRepository extends JpaRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomOrderByCreatedAt(ChatRoom chatRoom);
}
