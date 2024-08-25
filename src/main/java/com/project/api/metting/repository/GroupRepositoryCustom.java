package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupsByUserEmail(String email);
    List<GroupResponseDto> findGroupsByUserIdAndUserAuthHost(String userId);
    List<Group> findGroupsEntityByUserEmail(String email);
    // 무한스크롤 페이징 처리 및 필터링
    Page<MainMeetingListResponseDto> findGroupUsersByAllGroup(Pageable pageable,String gender,String region,Integer personnel,String email);
    Integer myChatListRequestDto(Group group);

}
