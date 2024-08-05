package com.project.api.metting.service;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.GroupRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupCreateService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;


    /**
     *
     * @param dto - 그룹 생성에 필요한 dto
     * @param tokenInfo - 현재 로그인한 사람의 정보가 들어가있는 token의 정보.
     */
    @Transactional
    public void createGroup(GroupCreateDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        User user = userRepository.findByEmail(tokenInfo.getEmail()).orElseThrow();

        // 유저가 이미 참여한 그룹의 개수 확인
        long groupCount = user.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .count();

        // 그룹 생성 제한 조건 검사
        if (groupCount >= 3) {
            throw new IllegalStateException("이미 세 개의 그룹에 참여 중입니다. 더 이상 그룹을 생성할 수 없습니다.");
        }


        // Group 엔터티 생성
        Group group = Group.builder()
                .groupName(dto.getGroupName())
                .groupGender(dto.getGroupGender())
                .groupPlace(dto.getGroupPlace())
                .maxNum(dto.getMaxNum())
                .build();

        // GroupUser 엔터티 생성
        GroupUser groupUser = GroupUser.builder()
                .group(group)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .auth(GroupAuth.HOST) // 그룹 생성자는 HOST로 설정
                .status(GroupStatus.REGISTERED) // 상태는 REGISTERED 설정
                .build();

        // Group 엔터티에 groupUsers 설정
        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser);
        group.setGroupUsers(groupUsers);

        // 그룹과 그룹 사용자 저장
        groupRepository.save(group);
    }
}