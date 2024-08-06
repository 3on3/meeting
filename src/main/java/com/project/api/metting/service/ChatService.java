package com.project.api.metting.service;

import com.project.api.metting.entity.ChatRoom;
import com.project.api.metting.repository.ChatRoomsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomsRepository chatRoomsRepository;

    public void createChatRoom(ChatRoom chatRoom) {
    }
}
