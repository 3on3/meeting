package com.project.api.metting.controller;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatRoomControllerTest {

    @Autowired
    private ChatRoomService chatRoomService;

}