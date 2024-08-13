package com.project.api.metting.service;

import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.dto.request.GroupDeleteRequestDto;
import com.project.api.metting.dto.request.GroupJoinRequestDto;
import com.project.api.metting.dto.request.GroupWithdrawRequestDto;
import com.project.api.metting.dto.response.GroupUsersViewListResponseDto;
import com.project.api.metting.dto.response.InviteResultResponseDto;
import com.project.api.metting.dto.response.InviteUsersViewResponseDto;
import com.project.api.metting.dto.response.UserResponseDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.GroupRepository;
import com.project.api.metting.repository.GroupUsersRepository;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.util.RandomUtil;
import com.project.api.metting.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final RedisUtil redisUtil;

    @Value("${frontend.url}")
    private String frontendUrl;

    private static final String INVITE_LINK_PREFIX = "group_invite_code:";

    /**
     * 그룹 생성 메서드
     *
     * @param dto       - 그룹 생성에 필요한 dto
     * @param tokenInfo - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     */
    @Transactional
    public void createGroup(GroupCreateDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        User user = userRepository.findByEmail(tokenInfo.getEmail()).orElseThrow();
        log.info("log info user - {}", user);

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

        // 그룹 저장
        groupRepository.save(group);

        // 그룹 저장 후 ID 로그
        log.info("Group ID after save: {}", group.getId());

        // 초대 코드 생성 및 저장
        try {
            String inviteCode = generateGroupInviteCode(group.getId());
            log.info("log info invite code - {}", inviteCode);
            group.setCode(inviteCode);

            // 업데이트된 그룹 저장
            groupRepository.save(group);
        } catch (Exception e) {
            log.error("Error generating invite code", e);
        }
    }

    /**
     * 그룹 초대 코드 생성 메서드
     *
     * @param groupId - 그룹 ID
     * @return - 생성된 초대 코드
     */
    @Transactional
    public String generateGroupInviteCode(String groupId) {
        validateExistGroup(groupId);
        log.info("Generating invite link for group ID: {}", groupId);

        final Optional<String> existingCode = redisUtil.getData(INVITE_LINK_PREFIX + groupId, String.class);
        log.info("Existing invite code: {}", existingCode.orElse("No existing invite code"));

        String inviteCode;
        if (existingCode.isEmpty()) {
            inviteCode = RandomUtil.generateRandomCode('0', 'z', 50);
            log.info("Generated random code: {}", inviteCode);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX + inviteCode, groupId, RedisUtil.toTomorrow() * 1000);
            log.info("Invite code set in Redis with expiration: {}", inviteCode);
        } else {
            inviteCode = existingCode.get();
        }

        String inviteLink = "http://localhost:3000/group/join/invite?code=" + inviteCode;
        log.info("Generated invite link: {}", inviteLink);
        return inviteLink;
    }

    private void validateExistGroup(String groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalStateException("해당 그룹을 찾을 수 없습니다.");
        }
    }

    /**
     * 그룹 가입 신청 메서드
     *
     * @param dto       - 가입 신청에 필요한 dto
     * @param tokenInfo - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     */
    @Transactional
    public void joinGroup(GroupJoinRequestDto dto, TokenUserInfo tokenInfo) {
        User user = userRepository.findByEmail(tokenInfo.getEmail()).orElseThrow();
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new IllegalStateException("해당 그룹을 찾을 수 없습니다."));

        // 유저가 이미 해당 그룹에 가입 신청했는지 확인
        boolean alreadyRequested = groupUsersRepository.existsByUserAndGroupAndStatus(user, group, GroupStatus.INVITING);
        if (alreadyRequested) {
            throw new IllegalStateException("이미 해당 그룹에 가입 신청하셨습니다.");
        }

        // 유저가 이미 해당 그룹에 가입되어 있는지 확인
        boolean alreadyJoined = groupUsersRepository.existsByUserAndGroup(user, group);
        if (alreadyJoined) {
            throw new IllegalStateException("이미 해당 그룹에 가입되어 있습니다.");
        }

        long currentUserCount = groupUsersRepository.countByGroupAndStatus(group, GroupStatus.REGISTERED);
        log.info("currrent user count - {}", currentUserCount);
        if (currentUserCount >= group.getMaxNum()) {
            throw new IllegalStateException("해당 그룹의 최대 인원 수를 초과했습니다.");
        }
        // GroupUser 엔티티 생성
        GroupUser groupUser = GroupUser.builder()
                .group(group)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .auth(GroupAuth.MEMBER) // 기본적으로 MEMBER로 설정
                .status(GroupStatus.INVITING) // 상태는 INVITING으로 설정
                .build();

        groupUsersRepository.save(groupUser);
    }

    /**
     * 초대 코드로 그룹에 가입하는 메서드
     *
     * @param inviteCode - 초대 코드
     * @param tokenInfo  - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     */
    public InviteResultResponseDto joinGroupWithInviteCode(String inviteCode, TokenUserInfo tokenInfo) {
        log.info("Attempting to join group with invite code: {}", inviteCode);
        String groupId = redisUtil.getData(INVITE_LINK_PREFIX + inviteCode, String.class)
                .orElseThrow(() -> new IllegalStateException("더 이상 존재하지 않는 가입 코드입니다. 다시 확인해주세요."));

        User loginUser = userRepository.findByEmail(tokenInfo.getEmail()).orElseThrow();
        // 유저가 이미 참여한 그룹의 개수 확인
        long groupCount = loginUser.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .count();

        // 그룹 생성 제한 조건 검사
        if (groupCount >= 3) {
            throw new IllegalStateException("이미 세 개의 그룹에 참여 중입니다. 더 이상 그룹에 참여신청이 불가능합니다.");
        }

        log.info("groupId info - {}", groupId);
        Group findGroup = groupRepository.findById(groupId).orElseThrow(IllegalStateException::new);


        User findUser = userRepository.findById(tokenInfo.getUserId()).orElseThrow(IllegalStateException::new);

        if (findGroup.getGroupGender() != findUser.getGender()) {
            throw new IllegalStateException("해당 그룹은 " +findGroup.getGroupGender()+ "성만 입장가능한 그룹입니다.");
        }


        User user = userRepository.findByEmail(tokenInfo.getEmail())
                .orElseThrow(() -> new IllegalStateException("해당 유저를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("해당 그룹을 찾을 수 없습니다."));

        // 유저가 이미 해당 그룹에 가입 신청했는지 확인
        boolean alreadyRequested = groupUsersRepository.existsByUserAndGroupAndStatus(user, group, GroupStatus.INVITING);
        if (alreadyRequested) {
            throw new IllegalStateException("이미 해당 그룹에 가입 신청을 완료하였습니다.");
        }

        // 유저가 이미 해당 그룹에 가입되어 있는지 확인
        boolean alreadyJoined = groupUsersRepository.existsByUserAndGroupAndStatus(user, group, GroupStatus.REGISTERED);
        if (alreadyJoined) {
            throw new IllegalStateException("이미 해당 그룹에 가입되어 있는 상태입니다.");
        }

        // GroupUser 엔티티 생성
        GroupUser groupUser = GroupUser.builder()
                .group(group)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .auth(GroupAuth.MEMBER) // 기본적으로 MEMBER로 설정
                .status(GroupStatus.INVITING) // 상태는 REGISTERED로 설정
                .build();

        groupUsersRepository.save(groupUser);

        InviteResultResponseDto dto = InviteResultResponseDto.builder()
                .groupName(group.getGroupName())
                .build();
        return dto;
    }

    /**
     * 그룹 가입 신청 목록 조회 메서드
     *
     * @param groupId   - 그룹 ID
     * @param tokenInfo - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     * @return - 그룹 가입 신청 목록
     */
    public List<InviteUsersViewResponseDto> getJoinRequests(String groupId, TokenUserInfo tokenInfo) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("해당 그룹을 찾을 수 없습니다."));


        // 그룹 생성자인지 확인
        boolean isHost = group.getGroupUsers().stream()
                .anyMatch(groupUser -> groupUser.getUser().getId().equals(tokenInfo.getUserId()) && groupUser.getAuth() == GroupAuth.HOST);

        if (!isHost) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        List<GroupUser> groupUsers = groupUsersRepository.findByGroupAndStatus(group, GroupStatus.INVITING);
        return groupUsers.stream()
                .map(InviteUsersViewResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 가입 신청 거절 메서드
     *
     * @param groupUserId - 그룹 사용자 ID
     * @param tokenInfo   - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     */
    @Transactional
    public void cancelJoinRequest(String groupUserId, TokenUserInfo tokenInfo) {
        GroupUser groupUser = groupUsersRepository.findById(groupUserId)
                .orElseThrow(() -> new IllegalStateException("가입 신청을 찾을 수 없습니다."));
        // 거절 처리
        groupUser.setStatus(GroupStatus.CANCELLED);
        groupUsersRepository.save(groupUser);
    }

    /**
     * 그룹 가입 신청 수락 메서드
     *
     * @param groupUserId - 그룹 사용자 ID
     * @param tokenInfo   - 현재 로그인한 사람의 정보가 들어가 있는 token의 정보.
     */
    @Transactional
    public void acceptJoinRequest(String groupUserId, TokenUserInfo tokenInfo) {
        GroupUser groupUser = groupUsersRepository.findById(groupUserId)
                .orElseThrow(() -> new IllegalStateException("가입 신청을 찾을 수 없습니다."));


        long currentUserCount = groupUsersRepository.countByGroupAndStatus(groupUser.getGroup(), GroupStatus.REGISTERED);
        log.info("currrent user count - {}", currentUserCount);
        if (currentUserCount >= groupUser.getGroup().getMaxNum()) {
            throw new IllegalStateException("해당 그룹의 최대 인원 수를 초과했습니다.");
        }


        // 수락 처리
        groupUser.setStatus(GroupStatus.REGISTERED);
        groupUsersRepository.save(groupUser);
    }


    /**
     * 그룹에 참여중인 유저 정보 전부 조회하기
     *
     * @param groupId - 그룹의 ID
     * @return 그룹의 유저 정보 목록
     */
    @Transactional
    public ResponseEntity<?> getGroupUsers(String groupId, @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        log.info("groupId info - {}", groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("그룹을 찾을 수 없습니다."));

        List<GroupUser> groupUsers = groupUsersRepository.findByGroupAndStatus(group, GroupStatus.REGISTERED);
        GroupUser findGroupHost = groupUsersRepository.findByGroupAndAuth(group, GroupAuth.HOST);
        log.info("find group users list - {}", groupUsers);

        List<UserResponseDto> users = groupUsers.stream()
                .map(groupUser -> new UserResponseDto(groupUser.getUser()))
                .collect(Collectors.toList());

        // 멤버의 평균 나이 계산
        int averageAge = (int) groupUsers.stream()
                .mapToLong(groupUser -> {
                    Date birthDate = groupUser.getUser().getBirthDate();
                    LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return ChronoUnit.YEARS.between(birthLocalDate, LocalDate.now()) + 2;
                })
                .average()
                .orElse(0);

        log.info("average Age info - {}", averageAge);

        // 주최자의 만남 위치
        Place meetingPlace = group.getGroupPlace();

        //rㅡ룹 이름
        String groupName = group.getGroupName();

        //그룹 초대 코드
        String code = group.getCode();

        // 전체 멤버 수
        int totalMembers = groupUsers.size();

        //그룹 리더 아이디
        String findHostId = findGroupHost.getUser().getId();


        //로그인한 유저의 그룹 권한
        GroupUser currentUser = groupUsers.stream()
                .filter(groupUser -> groupUser.getUser().getId().equals(tokenUserInfo.getUserId()))
                .findFirst()
                .orElse(null);

        String groupAuth = (currentUser != null) ? currentUser.getAuth().getDisplayName() : "USER"; // 기본값 설정

        //성별
        Gender gender = groupUsers.isEmpty() ? null : group.getGroupGender();

        log.info("gender info - {}", gender.getDisplayName()
        );
        GroupUsersViewListResponseDto generateGroupResponseData = GroupUsersViewListResponseDto.builder()
                .users(users)
                .averageAge(averageAge)
                .meetingPlace(meetingPlace.getDisplayName())
                .totalMembers(totalMembers)
                .groupName(groupName)
                .gender(gender != null ? gender.getDisplayName() : "N/A")
                .groupAuth(groupAuth)
                .inviteCode(code)
                .hostUser(findHostId)
                .groupSize(group.getMaxNum())
                .build();
        return ResponseEntity.ok(generateGroupResponseData);
    }

    /**
     * @param responseGroupId - 로그인한 유저의 token 정보
     */
    public User findByGroupHost(String responseGroupId) {

        Group group = groupRepository.findById(responseGroupId).orElseThrow(null);

        List<GroupUser> groupUsers = groupUsersRepository.findByGroup(group);

        for (GroupUser groupUser : groupUsers) {
            if (groupUser.getAuth() == GroupAuth.HOST) {
                return groupUser.getUser();
            }
        }

        return null;
    }

    @Transactional
    public void groupWithDraw(GroupWithdrawRequestDto dto, TokenUserInfo tokenInfo) {
        // 그룹 조회
        Group findGroup = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new IllegalStateException("그룹을 찾을 수 없습니다."));

        log.info("dto get group id - {}", dto.getGroupId());

        // 그룹과 유저를 기반으로 그룹 유저 조회
        GroupUser groupUser = groupUsersRepository.findByGroupAndUserId(findGroup, tokenInfo.getUserId())
                .orElseThrow(() -> new IllegalStateException("해당 그룹에 가입되어 있지 않습니다."));

        // 탈퇴 처리
        groupUser.setStatus(GroupStatus.WITHDRAW);
        groupUsersRepository.save(groupUser);
    }

    /**
     * @param dto           - 삭제 시킬 그룹의 group Id의 dto
     * @param tokenUserInfo - 로그인한 유저의 info
     */
    @Transactional
    public void groupDelete(GroupDeleteRequestDto dto, TokenUserInfo tokenUserInfo) {
        // 그룹을 찾기
        Group findGroup = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new IllegalStateException("그룹을 찾을 수 없습니다."));

        // 그룹과 유저를 기반으로 그룹 유저 조회
        GroupUser groupUser = groupUsersRepository.findByGroupAndUserId(findGroup, tokenUserInfo.getUserId())
                .orElseThrow(() -> new IllegalStateException("해당 그룹에 가입되어 있지 않습니다."));

        // 그룹 유저의 Auth를 조회해서 HOST인지 확인
        String groupUserAuth = groupUser.getAuth().getDisplayName();
        if (!groupUserAuth.equals(GroupAuth.HOST.getDisplayName())) {
            throw new IllegalStateException("그룹 삭제는 호스트만 가능합니다.");
        }

        // 그룹에 가입된 모든 유저를 조회
        List<GroupUser> groupUsers = groupUsersRepository.findByGroup(findGroup);

        // 모든 그룹 유저의 상태를 WITHDRAW로 업데이트
        for (GroupUser gu : groupUsers) {
            gu.setStatus(GroupStatus.WITHDRAW);
            groupUsersRepository.save(gu);
        }

        // 그룹 삭제 처리
        findGroup.setIsDeleted(true);
        groupRepository.save(findGroup);
    }

    // 그룹 아이디로 그룹 리턴
    public Group findGroupById(String groupId) {
       return groupRepository.findById(groupId).orElseThrow(null);
    }


    public List<GroupUser> findGroupUserList(User user) {
         return groupUsersRepository.findByUserAndStatus(user, GroupStatus.REGISTERED);
    }




}