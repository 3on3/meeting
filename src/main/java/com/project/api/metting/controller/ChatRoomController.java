package com.project.api.metting.controller;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final GroupMatchingService groupMatchingService;


    /**
     * 그룹 생성 요청
     * @param historyId - 매칭된 히스토리 아이디
     * @return - 메세지
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam String historyId) {

        chatRoomService.createChatRoom(historyId);

        return ResponseEntity.ok().body("채팅방 생성 완료");

    }
}
