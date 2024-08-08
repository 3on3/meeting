package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.UserProfile;

import java.util.List;

public interface UserProfileRepositoryCustom {
    UserProfile findByUserId (String userId);
}
