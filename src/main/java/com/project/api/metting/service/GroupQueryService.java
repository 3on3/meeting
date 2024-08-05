package com.project.api.metting.service;


import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupQueryService {

    private final GroupRepository groupRepository;

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        return groupRepository.findGroupsByUserEmail(email);
    }
}