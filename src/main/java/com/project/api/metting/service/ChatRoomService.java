package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.entity.ChatRoom;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupMatchingHistory;
import com.project.api.metting.entity.GroupProcess;
import com.project.api.metting.repository.ChatRoomsRepository;
import com.project.api.metting.repository.GroupMatchingHistoriesRepository;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.SimpleTimeZone;

import com.project.api.metting.dto.response.ChatUserResponseDto;
import com.project.api.metting.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomsRepository chatRoomsRepository;
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private final GroupRepository groupRepository;


    /**
     * 매칭 후 채팅룸 생성 함수
     * @param id - 매칭된 히스토리 아이디
     */
    @Transactional
    public void createChatRoom(String id) {
        try {
            GroupMatchingHistory matchingHistory = groupMatchingHistoriesRepository.findById(id).orElseThrow();

            boolean isProcessMatched = matchingHistory.getProcess().equals(GroupProcess.MATCHED);

            if (!isProcessMatched){
                throw new RuntimeException("수락된 매칭이 아닙니다.");
            }

            ChatRoom build = ChatRoom.builder()
                    // 주최자 그룹명으로 채팅 명 설정
                    .chatRoomName(matchingHistory.getResponseGroup().getGroupName())
                    .groupMatchingHistory(matchingHistory)
                    .build();

            chatRoomsRepository.save(build);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<User> findChatUsers(ChatUserResponseDto chatUserResponseDto) {


        return null;
    }
}
