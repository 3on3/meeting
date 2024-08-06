package com.project.api.metting.controller;

import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ChatMessageController {


    private final ChatMessageService chatMessageService;

    @GetMapping("/getMessage")
    public ResponseEntity<?> testChat(@RequestParam String chatRoomId) {

        System.out.println("chatRoomId = " + chatRoomId);

        List<ChatMessage> chatMessages = chatMessageService.finAllMessage(chatRoomId);

        return  ResponseEntity.ok().body(chatMessages);
    }
}
