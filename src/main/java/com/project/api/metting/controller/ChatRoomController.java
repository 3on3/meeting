package com.project.api.metting.controller;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final GroupMatchingService groupMatchingService;


    @PostMapping("/create")
    public void create(ChatRequestDto chatRequestDto) {




    }
}
