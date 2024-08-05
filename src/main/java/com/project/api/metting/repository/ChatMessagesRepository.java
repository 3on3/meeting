package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.entity.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessagesRepository extends JpaRepository<ChatMessage, String> {

    Optional<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
