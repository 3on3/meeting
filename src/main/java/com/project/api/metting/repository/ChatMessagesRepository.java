package com.project.api.metting.repository;

import com.project.api.metting.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, String> {
}
