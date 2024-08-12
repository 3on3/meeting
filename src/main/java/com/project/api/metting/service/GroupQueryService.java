package com.project.api.metting.service;


import com.project.api.auth.TokenProvider;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.repository.GroupRepository;
import com.project.api.metting.repository.GroupRepositoryCustomImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupQueryService {

    private final GroupRepository groupRepository;
    private final GroupRepositoryCustomImpl groupRepositoryCustom;

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        return groupRepository.findGroupsByUserEmail(email);
    }


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