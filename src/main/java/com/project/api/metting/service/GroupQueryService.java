package com.project.api.metting.service;


import com.project.api.auth.TokenProvider;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.GroupUser;
import com.project.api.metting.repository.GroupRepository;
import com.project.api.metting.repository.GroupRepositoryCustomImpl;
import com.project.api.metting.repository.GroupUsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupQueryService {

    private final GroupRepository groupRepository;
    private final GroupRepositoryCustomImpl groupRepositoryCustom;
    private final GroupUsersRepository groupUsersRepository;

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        return groupRepository.findGroupsByUserEmail(email);
    }


    /**
     * 매칭 조건이 일치하는 내가 속한 그룹 조회
     * @param tokenInfo - 유저 정보
     * @param id - 매칭하고자 하는 그룹 아이디
     * @return - 매칭 조건 일치하며 내가 속한 그룹들 dto
     */
    public List<GroupResponseDto> getMatchedMyGroups(TokenProvider.TokenUserInfo tokenInfo, String id) {
        List<GroupResponseDto> groupsByUserEmail = groupRepository.findGroupsByUserEmail(tokenInfo.getEmail());
        Group responseGroup = groupRepository.findById(id).orElseThrow();

        return groupsByUserEmail.stream()
                .filter(
                        groupResponseDto ->
                        groupResponseDto.getGroupGender() != responseGroup.getGroupGender()
                                && groupResponseDto.getGroupPlace() == responseGroup.getGroupPlace()
                                && groupResponseDto.getMemberCount() == responseGroup.getMaxNum()
                        ).collect(Collectors.toList());
    }
}