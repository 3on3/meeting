package com.project.api.metting.service;

import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;


    public UserProfile getUserProfile(String userId) {
        UserProfile byUserId = userProfileRepository.findByUserId(userId);
        if (byUserId.getProfileImg() == null) {
            throw new IllegalStateException("해당 유저는 프로필 설정을 하지 않았습니다.");
        }
        return byUserId;
    }
}



