package com.project.api.metting.repository;

import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;

import java.util.List;

public interface GroupRepositoryCustom {

    //    main meetingList DTO
    List<MainMeetingListResponseDto> filterGroupUsersByAllGroup(MainMeetingListFilterDto dto);

    List<GroupResponseDto> findGroupsByUserEmail(String email);

    List<MainMeetingListResponseDto> findGroupUsersByAllGroup();
}
