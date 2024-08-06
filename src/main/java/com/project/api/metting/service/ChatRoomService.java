package com.project.api.metting.service;

import com.project.api.metting.dto.response.ChatUserResponseDto;
import com.project.api.metting.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    public List<User> findChatUsers(ChatUserResponseDto chatUserResponseDto) {


        return null;
    }
}
