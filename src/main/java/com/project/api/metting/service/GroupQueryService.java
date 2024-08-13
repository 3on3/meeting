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


    public List<GroupResponseDto> getMatchedMyGroups(TokenProvider.TokenUserInfo tokenInfo, String id) {
        List<GroupResponseDto> groupsByUserEmail = groupRepository.findGroupsByUserEmail(tokenInfo.getEmail());
        Group responseGroup = groupRepository.findById(id).orElseThrow();

//        List<GroupUser> groupList = groupUsersRepository.findByGroup(responseGroup);
//        log.info("group List size - {}", groupList.size());
        return groupsByUserEmail.stream()
                .filter(
                        groupResponseDto ->
                        groupResponseDto.getGroupGender() != responseGroup.getGroupGender()
                                && groupResponseDto.getGroupPlace() == responseGroup.getGroupPlace()
                                && groupResponseDto.getMemberCount() == responseGroup.getMaxNum()
                        ).collect(Collectors.toList());
    }
}