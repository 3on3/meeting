package com.project.api.metting.controller;

import com.project.api.metting.dto.request.GroupCreateDto;
import com.project.api.metting.dto.request.GroupDeleteRequestDto;
import com.project.api.metting.dto.request.GroupJoinRequestDto;
import com.project.api.metting.dto.request.GroupWithdrawRequestDto;
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
@CrossOrigin
public class GroupController {
    private final GroupService groupService;

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

    @PostMapping("/join")
    public ResponseEntity<?> GroupJoin(@RequestBody GroupJoinRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        try {
            groupService.joinGroup(dto, tokenInfo);
            return ResponseEntity.ok().body("가입 신청이 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 참여 신청에 실패하였습니다. 다시 시도해주세요.");
        }
    }


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

    @GetMapping("/invite/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable String groupId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        List<InviteUsersViewResponseDto> joinRequests = groupService.getJoinRequests(groupId, tokenInfo);
        return ResponseEntity.ok().body(joinRequests);
    }

    @PostMapping("/join-requests/{groupUserId}/accept")
    public ResponseEntity<String> acceptJoinRequest(@PathVariable String groupUserId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {

        try {
        groupService.acceptJoinRequest(groupUserId, tokenInfo);
            return ResponseEntity.ok("성공적으로 그룹에 가입신청을 완료하였습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입신청 수락에 실패하였습니다.. 다시 시도해주세요..");
        }
    }


    @PostMapping("/withdraw")
    public ResponseEntity<Void> groupWithDraw(@RequestBody GroupWithdrawRequestDto dto, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        groupService.groupWithDraw(dto, tokenInfo);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/join-requests/{groupUserId}/cancel")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable String groupUserId, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        groupService.cancelJoinRequest(groupUserId, tokenInfo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join/invite")
    public ResponseEntity<?> joinGroupWithInviteCode(@RequestParam String code, @AuthenticationPrincipal TokenUserInfo tokenInfo) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 코드는 더 이상 존재하지 않습니다..");
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
}
