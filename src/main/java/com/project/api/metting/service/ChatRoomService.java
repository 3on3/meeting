package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.dto.request.ChatUserRequestDto;
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
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private final GroupRepository groupRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;


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
}
