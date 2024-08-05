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

    @GetMapping("/testChat")
    public ResponseEntity<?> testChat(@RequestParam String chatRoomId) {

        System.out.println("chatRoomId = " + chatRoomId);

        chatMessageService.finAllMessage(chatRoomId);

        return  ResponseEntity.ok().body(null);
    }
}
