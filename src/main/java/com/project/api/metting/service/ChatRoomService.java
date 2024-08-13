package com.project.api.metting.service;

import com.project.api.metting.dto.request.ChatRequestDto;
import com.project.api.metting.dto.request.ChatRoomRequestDto;
import com.project.api.metting.dto.request.ChatUserRequestDto;
import com.project.api.metting.dto.request.MyChatListRequestDto;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.project.api.metting.dto.response.ChatUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);
    private final ChatRoomsRepository chatRoomsRepository;
    private final GroupRepository groupRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final UserProfileRepository userProfileRepository;
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private final UserRepository userRepository;
    private final GroupRepositoryCustomImpl groupRepositoryCustom;


    /**
     * 매칭 후 채팅룸 생성 함수
     *
     * @param chatRoomRequestDto - 매칭된 히스토리 아이디
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto chatRoomRequestDto) {
        try {
            Group findRequestGroup = groupRepository.findById(chatRoomRequestDto.getRequestGroupId()).orElseThrow(null);
            Group findResponseGroup = groupRepository.findById(chatRoomRequestDto.getResponseGroupId()).orElseThrow(null);

            GroupMatchingHistory history = groupMatchingHistoriesRepository.findByResponseGroupAndRequestGroup(findResponseGroup, findRequestGroup);

            // 같은 그룹 사이에 채팅방생성 isDeleted = 0이면 불가 isDeleted = 1 이면 새로운 채팅방.
            boolean isProcessMatched = history.getProcess().equals(GroupProcess.MATCHED);

            if (!isProcessMatched) {
                throw new RuntimeException("수락된 매칭이 아닙니다.");
            }

            ChatRoom chatRoom = ChatRoom.builder().chatRoomName(findResponseGroup.getGroupName()).groupMatchingHistory(history).build();


            chatRoomsRepository.save(chatRoom);

            return ChatRoomResponseDto.builder().id(chatRoom.getId()).name(chatRoom.getChatRoomName()).historyID(chatRoom.getGroupMatchingHistory().getId()).build();
        } catch (Exception e) {
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

            if (userProfile == null) {
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

    /**
     * 채팅방 아이디로 채팅방 dto 반환
     * @param id - 채팅방 아이디
     * @return 채팅방 dto
     */
    public ChatRoomResponseDto findChatById(String id) {
        ChatRoom chatRoom = chatRoomsRepository.findById(id).orElseThrow();
        return ChatRoomResponseDto.builder().id(chatRoom.getId()).name(chatRoom.getChatRoomName()).historyID(chatRoom.getGroupMatchingHistory().getId()).build();
    }


//    public ChatRoomResponseDto findChatById(String id) {
//        // Optional을 사용하여 채팅방을 조회합니다.
//        Optional<ChatRoom> chatRoomOptional = chatRoomsRepository.findById(id);
//
//        // 채팅방이 존재하지 않는 경우 사용자 친화적인 메시지를 반환합니다.
//        if (chatRoomOptional.isEmpty()) {
//            // 채팅방을 찾을 수 없는 경우 사용자 친화적인 예외를 발생시킵니다.
//            throw new RuntimeException("해당 ID로 채팅방을 찾을 수 없습니다: " + id);
//        }
//
//        // 채팅방이 존재하는 경우, DTO로 변환하여 반환합니다.
//        ChatRoom chatRoom = chatRoomOptional.get();
//        return ChatRoomResponseDto.builder()
//                .id(chatRoom.getId())
//                .name(chatRoom.getChatRoomName())
//                .historyID(chatRoom.getGroupMatchingHistory().getId())
//                .build();
//    }


    public List<MyChatListRequestDto> findChatList(String userId) {

        User user = userRepository.findById(userId).orElseThrow();

        List<GroupUser> groupUsers = groupUsersRepository.findByUserAndStatus(user, GroupStatus.REGISTERED);

        System.out.println("groupUsers = " + groupUsers);

        List<Group> userGroups = new ArrayList<>();

        for (GroupUser groupUser : groupUsers) {
            if(!groupUser.getGroup().getIsDeleted()) {
                userGroups.add(groupUser.getGroup());
            }
        }

        System.out.println("userGroups = " + userGroups);

        List<GroupMatchingHistory> matchingHistories = new ArrayList<>();
        
        List<Group> matchingGroups = new ArrayList<>();

        for (Group userGroup : userGroups) {
            GroupMatchingHistory responseHistory = groupMatchingHistoriesRepository.findByResponseGroupAndProcess(userGroup, GroupProcess.MATCHED);
            GroupMatchingHistory requestHistory = groupMatchingHistoriesRepository.findByRequestGroupAndProcess(userGroup, GroupProcess.MATCHED);

            if(responseHistory != null) {
                matchingHistories.add(responseHistory);
                matchingGroups.add(responseHistory.getRequestGroup());
            }
            if(requestHistory != null) {
                matchingHistories.add(requestHistory);
                matchingGroups.add(requestHistory.getResponseGroup());
            }
        }

        System.out.println("matchingHistories = " + matchingHistories);

        List<MyChatListRequestDto> myChatListRequestDtoList = new ArrayList<>();

        for (int i = 0; i < matchingHistories.size(); i++) {



            GroupUser groupUser = groupUsersRepository.findByGroupAndAuth(matchingGroups.get(i), GroupAuth.HOST);

            User hostUser = userRepository.findById(groupUser.getUser().getId()).orElseThrow();

            MyChatListRequestDto myChatListRequestDto = MyChatListRequestDto.builder()
                    .chatRoomId(matchingHistories.get(i).getChatRoom().getId())
                    .groupName(matchingGroups.get(i).getGroupName())
                    .groupPlace(matchingGroups.get(i).getGroupPlace())
                    .maxNum(matchingGroups.get(i).getMaxNum())
                    .gender(matchingGroups.get(i).getGroupGender())
                    .major(hostUser.getMajor())
                    .build();

            // 그룹의 평균나이 계산
            groupRepositoryCustom.myChatListRequestDto(matchingGroups.get(i), myChatListRequestDto);

            myChatListRequestDtoList.add(myChatListRequestDto);
        }

        System.out.println("myChatListRequestDtoList = " + myChatListRequestDtoList);


        return myChatListRequestDtoList;

    }
}
