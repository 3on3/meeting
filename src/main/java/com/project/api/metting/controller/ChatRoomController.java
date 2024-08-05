package com.project.api.metting.controller;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.entity.ChatRoom;
import com.project.api.metting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;


    @PostMapping("/create")
    public void create(ChatRequestDto chatRequestDto) {

        ChatRoom.builder()
                .chatRoomName(chatRequestDto.getName())
                .build();

    }
}
