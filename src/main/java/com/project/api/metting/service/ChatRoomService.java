package com.project.api.metting.service;

import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.ChatRoomResponseDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private final GroupRepository groupRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final UserProfileRepository userProfileRepository;
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final GroupMatchingService groupMatchingService;
    private final UserService userService;

    /**
     * 매칭 후 채팅룸 생성 함수
     *
     * @param chatRoomRequestDto - 매칭된 히스토리 아이디
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto chatRoomRequestDto) {
        try {

            Group findRequestGroup = groupService.findGroupById(chatRoomRequestDto.getRequestGroupId());
            Group findResponseGroup = groupService.findGroupById(chatRoomRequestDto.getResponseGroupId());

            GroupMatchingHistory history = groupMatchingService.findByResponseGroupAndRequestGroup(findResponseGroup, findRequestGroup);

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

    public FindChatUserRequestDto findChatUsers(ChatUserResponseDto chatUserResponseDto) {

        // 채팅방 아이디로 채팅방 정보 가져오기
        ChatRoom chatRoom = chatRoomsRepository.findById(chatUserResponseDto.getChatroomId()).orElseThrow();


        // 채팅방의 매칭 히스토리 가져오기
        GroupMatchingHistory groupMatchingHistory = chatRoom.getGroupMatchingHistory();

        // 매칭 히스토리에서 리스폰, 리퀘스트 그룹 가져오기
        Group group1 = groupMatchingHistory.getResponseGroup();
        String responseHostUserId = groupUsersRepository.findByGroupAndAuth(group1, GroupAuth.HOST).getUser().getId();
        Group group2 = groupMatchingHistory.getRequestGroup();
        String requestHostUserId = groupUsersRepository.findByGroupAndAuth(group2, GroupAuth.HOST).getUser().getId();

        // 각 그룹에 존재하는 유저정보 가져오기
        List<GroupUser> responseGroupUsers = groupUsersRepository.findByGroupAndStatus(group1, GroupStatus.REGISTERED);
        List<GroupUser> requestGroupUsers = groupUsersRepository.findByGroupAndStatus(group2, GroupStatus.REGISTERED);


        List<ChatUserRequestDto> responseChatUser = findChatUser(responseGroupUsers);
        List<ChatUserRequestDto> requestChatUser = findChatUser(requestGroupUsers);

        return FindChatUserRequestDto.builder()
                .requestChatUser(requestChatUser)
                .responseChatUser(responseChatUser)
                .responseGroupName(group1.getGroupName())
                .requestGroupName(group2.getGroupName())
                .responseHostUserId(responseHostUserId)
                .requestHostUserId(requestHostUserId)
                .build();

    }

    public List<ChatUserRequestDto> findChatUser(List<GroupUser> groupUsers) {
        List<User> users = new ArrayList<>();

        for (GroupUser groupUser : groupUsers) {
            users.add(groupUser.getUser());
        }

        List<ChatUserRequestDto> chatUsers = new ArrayList<>();

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
                    .userId(user.getId())
                    .userNickname(user.getNickname())
                    .build();

            chatUsers.add(chatUserRequestDto);
        }

        return chatUsers;
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

    public List<MyChatListRequestDto> findChatList(String userId) {

        // 로그인한 유저 정보 가져오기
        User user = userService.findUser(userId);

        // 로그인한 유저가 그룹가입기록 조회(GroupStatus가 REGISTERED 인것만)
        List<GroupUser> groupUsers = groupService.findGroupUserList(user);

        System.out.println("groupUsers = " + groupUsers);

        // 그룹가입기록 조회를 이용하여 그룹 가져오기 (그룹이 삭제되지 않은것만)
        List<Group> userGroups = new ArrayList<>();

        for (GroupUser groupUser : groupUsers) {
            if(!groupUser.getGroup().getIsDeleted()) {
                userGroups.add(groupUser.getGroup());
            }
        }

        System.out.println("userGroups = " + userGroups);

        List<GroupMatchingHistory> matchingHistories = new ArrayList<>();

        // 상대 그룹정보를 담기위한 그룹 리스트
        List<Group> matchingGroups = new ArrayList<>();

        // 유저가 가입한 그룹을 이용하여 GroupMatchingHistory를 가져오기 (Process가 MATCHED인것만)
        // 유저 그룹이 response그룹에 들어있으면 request그룹을 저장한다. (상대방 그룹의 정보를 채팅리스트에 넣기 위해서)
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


        List<MyChatListRequestDto> myChatListRequestDtoList = new ArrayList<>();

        // 유저의 그룹 매칭 히스토리를 DTO로 변환
        for (int i = 0; i < matchingHistories.size(); i++) {

            // 상대방 그룹에 존재하는 멤버수
            int groupMember = groupUsersRepository.findByGroupAndStatus(matchingGroups.get(i), GroupStatus.REGISTERED).size();

            // 채팅방에 존재하는 멤버수
            int chatMemberCount = groupUsersRepository.findByGroupAndStatus(userGroups.get(i), GroupStatus.REGISTERED).size() + groupMember;

            // 상대그룹의 HOST유저
            GroupUser groupUser = groupUsersRepository.findByGroupAndAuth(matchingGroups.get(i), GroupAuth.HOST);

            // 상대그룹의 HOST유저의 유저정보
            User hostUser = groupUser.getUser();

            Integer averageAge = groupRepository.myChatListRequestDto(matchingGroups.get(i));

            MyChatListRequestDto myChatListRequestDto = MyChatListRequestDto.builder()
                    .chatRoomId(matchingHistories.get(i).getChatRoom().getId())
                    .groupName(matchingGroups.get(i).getGroupName())
                    .groupPlace(matchingGroups.get(i).getGroupPlace())
                    .groupMemberCount(groupMember)
                    .gender(matchingGroups.get(i).getGroupGender())
                    .major(hostUser.getMajor())
                    .age(averageAge)
                    .chatMemberCount(chatMemberCount)
                    .build();


            myChatListRequestDtoList.add(myChatListRequestDto);
        }

        System.out.println("myChatListRequestDtoList = " + myChatListRequestDtoList);


        return myChatListRequestDtoList;

    }
}
