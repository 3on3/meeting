package com.project.api.metting.controller;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.dto.request.ChatUserRequestDto;
import com.project.api.metting.dto.response.ChatUserResponseDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.ChatRoomsRepository;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    // 채팅방에 있는 유저 정보 가져오기이이이ㅣㅣㅣㅣㅣㅣㅣㅣ
    @PostMapping("/chatUsers")
    public ResponseEntity<?> chatUsers (@RequestBody ChatUserResponseDto chatUserDto){

        List<ChatUserRequestDto> chatUserList = chatRoomService.findChatUsers(chatUserDto);

        System.out.println("chatUserList = " + chatUserList);

        return ResponseEntity.ok().body(chatUserList);
    }
}
