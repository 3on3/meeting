package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.metting.dto.request.ChatMessageRequestDto;
import com.project.api.metting.dto.response.ChatMessageResponseDto;
import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/testChat")
public class ChatMessageController {


    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @GetMapping("/getMessage")
    public ResponseEntity<?> testChat(@RequestParam String chatRoomId) {

        List<ChatMessageRequestDto> chatMessages = chatMessageService.finAllMessage(chatRoomId);

        return  ResponseEntity.ok().body(chatMessages);
    }

    @GetMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageResponseDto chatMessageResponseDto, @AuthenticationPrincipal TokenProvider.TokenUserInfo tokenUserInfo) {

        String userId = tokenUserInfo.getUserId();

        ChatMessageRequestDto chatMessage = chatMessageService.saveChatMessage(chatMessageResponseDto, userId);

        return ResponseEntity.ok().body(chatMessage);
    }


}
