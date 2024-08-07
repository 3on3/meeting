package com.project.api.metting.service;


import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ChatRoomServiceTest {
    @Autowired
    private  ChatRoomService chatRoomService;
    @Autowired
    private GroupMatchingService groupMatchingService;


    @Test
    @DisplayName("")
    void createChatRoom () {
        //given
        String historyId = "fe771b63-e666-4750-99c2-3a18b13677f9";
        //when
        groupMatchingService.acceptRequest(historyId);
        chatRoomService.createChatRoom(historyId);

        //then
    }

}