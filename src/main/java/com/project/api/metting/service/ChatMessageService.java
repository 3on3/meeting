package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.metting.dto.response.ChatMessageResponseDto;
import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.entity.ChatRoom;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.ChatMessagesRepository;
import com.project.api.metting.repository.ChatRoomsRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageService.class);
    private final ChatMessagesRepository chatMessagesRepository;
    private final ChatRoomsRepository chatRoomsRepository;
    private final UserRepository userRepository;

    public List<ChatMessage> finAllMessage(String chatRoomId) {

        System.out.println("chatRoomId = " + chatRoomId);
        Optional<ChatRoom> chatRoom = chatRoomsRepository.findById(chatRoomId);
        System.out.println("chatRoom = " + chatRoom);

        List<ChatMessage> chatMessage = chatMessagesRepository.findByChatRoom(chatRoom.orElse(null));
        System.out.println("chatMessage = " + chatMessage);

        return chatMessage;
    }

    public ChatMessage saveChatMessage(ChatMessageResponseDto chatMessageResponseDto, String userId) {

        User user = userRepository.findById(userId).orElse(null);
        ChatRoom chatRoom = chatRoomsRepository.findById(chatMessageResponseDto.getRoomId()).orElse(null);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .messageContent(chatMessageResponseDto.getMessage())
                .build();

        ChatMessage savedMessage = chatMessagesRepository.save(chatMessage);
        return savedMessage;
    }
}
