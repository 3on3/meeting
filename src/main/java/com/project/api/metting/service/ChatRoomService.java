package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.dto.request.ChatRoomRequestDto;
import com.project.api.metting.dto.request.ChatUserRequestDto;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.SimpleTimeZone;

import com.project.api.metting.dto.response.ChatUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomsRepository chatRoomsRepository;
    private final GroupRepository groupRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final UserProfileRepository userProfileRepository;
    private final GroupMatchingHistoriesCustomImpl groupMatchingHistoriesCustomImpl;


    /**
     * 매칭 후 채팅룸 생성 함수
     * @param chatRoomRequestDto - 매칭된 히스토리 아이디
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto chatRoomRequestDto) {
        try {
            Group findRequestGroup = groupRepository.findById(chatRoomRequestDto.getRequestGroupId()).orElseThrow(null);
            Group findResponseGroup = groupRepository.findById(chatRoomRequestDto.getResponseGroupId()).orElseThrow(null);
            List<GroupMatchingHistory> histories = groupMatchingHistoriesCustomImpl.findByResponseGroupId(findResponseGroup.getId());
            GroupMatchingHistory history = histories.stream().filter(groupMatchingHistory -> groupMatchingHistory.getRequestGroup().getId().equals(chatRoomRequestDto.getRequestGroupId())).findFirst().orElseThrow(null);

            // 같은 그룹 사이에 채팅방생성 isDeleted = 0이면 불가 isDeleted = 1 이면 새로운 채팅방.
            boolean isProcessMatched = history.getProcess().equals(GroupProcess.MATCHED);

            if (!isProcessMatched){
                throw new RuntimeException("수락된 매칭이 아닙니다.");
            }

            ChatRoom chatRoom = ChatRoom.builder().chatRoomName(findResponseGroup.getGroupName()).groupMatchingHistory(history).build();


            chatRoomsRepository.save(chatRoom);

            return ChatRoomResponseDto.builder().id(chatRoom.getId()).name(chatRoom.getChatRoomName()).historyID(chatRoom.getGroupMatchingHistory().getId()).build();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public List<ChatUserRequestDto> findChatUsers(ChatUserResponseDto chatUserResponseDto) {

        // 채팅방 아이디로 채팅방 정보 가져오기
        ChatRoom chatRoom = chatRoomsRepository.findById(chatUserResponseDto.getChatroomId()).orElseThrow();


        // 채팅방의 매칭 히스토리 가져오기
        GroupMatchingHistory groupMatchingHistory = chatRoom.getGroupMatchingHistory();

        // 매칭 히스토리에서 리스폰, 리퀘스트 그룹 가져오기
        Group group1 = groupMatchingHistory.getResponseGroup();
        Group group2 = groupMatchingHistory.getRequestGroup();

        // 각 그룹에 존재하는 유저정보 가져오기
        List<GroupUser> groupUsers = groupUsersRepository.findByGroup(group1);
        List<GroupUser> groupUsers2 = groupUsersRepository.findByGroup(group2);

        groupUsers.addAll(groupUsers2);

        List<User> users = new ArrayList<>();

        for (GroupUser groupUser : groupUsers) {
            users.add(groupUser.getUser());
        }

        List<ChatUserRequestDto> chatUserRequestDtoList = new ArrayList<>();

        for (User user : users) {
            UserProfile userProfile = userProfileRepository.findByUser(user);

            String imgUrl;

            if(userProfile == null){
                imgUrl = "imgOriginUrl";
            } else {
                imgUrl = userProfile.getProfileImg();
            }

            ChatUserRequestDto chatUserRequestDto = ChatUserRequestDto.builder()
                    .imgUrl(imgUrl)
                    .univ(user.getUnivName())
                    .major(user.getMajor())
                    .build();

            chatUserRequestDtoList.add(chatUserRequestDto);
        }

        return chatUserRequestDtoList;
    }

    public ChatRoomResponseDto findChatById(String id) {
        ChatRoom chatRoom = chatRoomsRepository.findById(id).orElseThrow();
        return ChatRoomResponseDto.builder().id(chatRoom.getId()).name(chatRoom.getChatRoomName()).historyID(chatRoom.getGroupMatchingHistory().getId()).build();
    }
}
