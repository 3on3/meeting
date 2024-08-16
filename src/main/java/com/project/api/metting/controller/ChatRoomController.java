package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.dto.response.ChatUserResponseDto;
import com.project.api.metting.entity.ChatRoom;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.ChatRoomsRepository;
import com.project.api.metting.service.ChatRoomService;
import com.project.api.metting.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final GroupMatchingService groupMatchingService;


    /**
     * 채팅방 겟 요청
     * @param id - 채팅방 아이디
     * @return - 채팅방 정보 dto
     */
    @GetMapping("{id}")
    public ResponseEntity<?> chat(@PathVariable String id){

        ChatRoomResponseDto chatRoomResponseDto = chatRoomService.findChatById(id);

        return ResponseEntity.ok().body(chatRoomResponseDto);
    }

    /**
     * 그룹 생성 요청
     * @param chatRoomRequestDto - 매칭된 히스토리 아이디
     * @return - 메세지
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {

        ChatRoomResponseDto chatRoom = chatRoomService.createChatRoom(chatRoomRequestDto);

        return ResponseEntity.ok().body(chatRoom);

    }


    // 채팅방에 있는 유저 정보 가져오기이이이ㅣㅣㅣㅣㅣㅣㅣㅣ
    @PostMapping("/chatUsers")
    public ResponseEntity<?> chatUsers (@RequestBody ChatUserResponseDto chatUserDto){

        FindChatUserRequestDto chatUserList = chatRoomService.findChatUsers(chatUserDto);

        System.out.println("chatUserList = " + chatUserList);

        return ResponseEntity.ok().body(chatUserList);
    }

    @PostMapping("/myChatList")
    public ResponseEntity<?> myChatList(@AuthenticationPrincipal TokenProvider.TokenUserInfo tokenUserInfo){

        String userId = tokenUserInfo.getUserId();

        List<MyChatListRequestDto> chatList = chatRoomService.findChatList(userId);

        return ResponseEntity.ok().body(chatList);
    }
}
