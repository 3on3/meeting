package com.project.api.metting.controller;

import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.InviteCodeResponseDto;
import com.project.api.metting.dto.response.InviteResultResponseDto;
import com.project.api.metting.dto.response.InviteUsersViewResponseDto;
import com.project.api.metting.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.api.auth.TokenProvider.TokenUserInfo;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;


    /**
     *
     * @param dto - 그룹 생성에 필요한 dto
     * @param tokenInfo - 로그인한 유저의 token
     * @return - 성공하면 text 전송
     */
    @PostMapping("/create")
    public ResponseEntity<?> GroupCreate(@RequestBody GroupCreateDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.createGroup(dto, tokenInfo);
            return ResponseEntity.ok().body("그룹 생성에 성공하였습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 생성에 실패하였습니다. 다시 시도해주세요.");
        }
    }


    /**
     *
     * @param dto - 그룹 삭제에 필요한 dto
     * @param tokenInfo - 로그인한 유저의 token 정보
     * @return - 성공하면 그룹 삭제 성공 텍스트 전송
     */
    @PostMapping("/delete")
    public ResponseEntity<?> GroupDelete(@RequestBody GroupDeleteRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.groupDelete(dto, tokenInfo);
            return ResponseEntity.ok().body("그룹 삭제가 완료 되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 참여 신청에 실패하였습니다. 다시 시도해주세요.");
        }
    }


    /**
     *
     * @param groupId - 그룹에 참여신청한 유저의 목록
     * @param tokenInfo - 로그인한 유저의 token 정보
     * @return - 성공한 유저 목록
     */
    @GetMapping("/invite/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable String groupId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        List<InviteUsersViewResponseDto> joinRequests = groupService.getJoinRequests(groupId, tokenInfo);
        return ResponseEntity.ok().body(joinRequests);
    }


    /**
     *
     * @param groupUserId - 그룹에 참여신청한 유저 수락하는 메서드
     * @param tokenInfo - 로그인한 유저의 토큰 정보
     * @return - 성공하면 텍스트 리턴
     */
    @PostMapping("/join-requests/{groupUserId}/accept")
    public ResponseEntity<String> acceptJoinRequest(@PathVariable String groupUserId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {

        try {
        groupService.acceptJoinRequest(groupUserId, tokenInfo);
            return ResponseEntity.ok("그룹 가입 신청을 수락하였습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입신청 수락에 실패하였습니다. 다시 시도해주세요.");
        }
    }


    /**
     *
     * @param dto - 그룹 탈퇴에 필요한 dto 정보
     * @param tokenInfo - 로그인 한 유저의 토큰 정보
     * @return - 성공 ok 전송
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Void> groupWithDraw(@RequestBody GroupWithdrawRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        groupService.groupWithDraw(dto, tokenInfo);
        return ResponseEntity.ok().build();
    }


    /**
     *
     * @param groupUserId - 가입신청을 거절한 유저의 groupuserId
     * @param tokenInfo - 로그인한 유저의 토큰 정보
     * @return - 거절 텍스트 리턴
     */
    @PostMapping("/join-requests/{groupUserId}/cancel")
    public ResponseEntity<?> rejectJoinRequest(@PathVariable String groupUserId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.cancelJoinRequest(groupUserId, tokenInfo);
            return ResponseEntity.ok("그룹 가입 신청을 거절하였습니다.");
        }  catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입신청을 거절에 실패하였습니다.");
        }
    }


    /**
     *
     * @param dto - 그룹에서 추방할 정보의 dto
     * @param tokenInfo - 로그인 한 유저의 dto
     * @return - 성공하면 text 전송
     */
    @PostMapping("/exile")
    public ResponseEntity<?> exileUser(@RequestBody GroupExileDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        log.info("dto info 123 - {}", dto.getGroupId());
        try {
            groupService.deleteUserByHost(dto, tokenInfo);
            return ResponseEntity.ok("성공적으로 그룹에서 추방하였습니다.");
        }  catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("유저 추방에 실패하였습니다.");
        }
    }


    /**
     *
     * @param code - 해당 그룹에 가입할 수 있는 초대코드
     * @param tokenInfo - 로그인 한 유저의 토큰 정보
     * @return - 성공하면 그룹이름을 리턴
     */
    @PostMapping("/join/invite")
    public ResponseEntity<?> joinGroupWithInviteCode(@RequestParam String code, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 코드는 더 이상 존재하지 않습니다.");
        }
        try {
            InviteResultResponseDto resultDto = groupService.joinGroupWithInviteCode(code, tokenInfo);
            return ResponseEntity.ok(resultDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입신청에 실패하였습니다. 다시 시도해주세요.");
        }
    }




    /**
     * 특정 그룹 사용자 ID에 대한 그룹 목록을 가져오는 엔드포인트
     *
     * @param groupUserId - 그룹 사용자 ID
     * @return - 그룹 목록
     */
    @GetMapping("/{groupUserId}")
    public ResponseEntity<?> getGroupList(@PathVariable String groupUserId, @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        return groupService.getGroupUsers(groupUserId, tokenUserInfo);
    }


    /**
     * 초대코드를 생성하는 메서드
     * @param dto - 초대코드 생성에 필요한 groupId dto,
     * @param tokenInfo - 로그인한 유저의 정보
     * @return - 초대링크랑 남은시간 리턴
     */
    @PostMapping("/inviteCodeGenerate")
    public ResponseEntity<?> inviteCodeSelect(@RequestBody GroupDeleteRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        InviteCodeResponseDto inviteCodeResponseDto = groupService.generateGroupInviteCode(dto.getGroupId());
        return ResponseEntity.ok().body(inviteCodeResponseDto);
    }

}
