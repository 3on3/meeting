package com.project.api.metting.repository;

import com.project.api.metting.dto.request.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


interface GroupMatchingHistoriesCustom {
    List<ChatRequestDto> findGroupById(String groupId);
}
