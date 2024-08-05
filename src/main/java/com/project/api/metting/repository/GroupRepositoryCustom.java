package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Group;

import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupsByUserEmail(String email);
}
