package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;

import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupsByUserEmail(String email);

    List<MainMeetingListResponseDto> findGroupUsersByAllGroup();
}
