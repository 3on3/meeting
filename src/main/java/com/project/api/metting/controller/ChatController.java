package com.project.api.metting.controller;

import com.project.api.metting.dto.request.MessageRequestDto;
import com.project.api.metting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/enter")
    @SendTo("/chat/enter")
    public void sendMessage (MessageRequestDto messageRequestDto){

    }

}
