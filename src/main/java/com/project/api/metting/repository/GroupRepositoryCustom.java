package com.project.api.metting.repository;

import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupRepositoryCustom {

    //    main meetingList DTO
    Page<MainMeetingListResponseDto> filterGroupUsersByAllGroup(MainMeetingListFilterDto dto);

    List<GroupResponseDto> findGroupsByUserEmail(String email);

    Page<MainMeetingListResponseDto> findGroupUsersByAllGroup(Pageable pageable);
}
