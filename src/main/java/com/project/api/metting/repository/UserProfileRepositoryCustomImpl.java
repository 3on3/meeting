package com.project.api.metting.repository;

import com.project.api.metting.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserProfileRepositoryCustomImpl implements UserProfileRepositoryCustom{
    private final JPAQueryFactory factory;

    QUser user = QUser.user;
    QUserProfile userProfile = QUserProfile.userProfile;

    @Override
    public UserProfile findByUserId(String userId) {
        return factory.selectFrom(userProfile)
                .join(userProfile.user, user)  // UserProfile 엔티티와 User 엔티티를 조인
                .where(user.id.eq(userId))  // userId로 필터링
                .fetchOne();  // 단일 결과를 반환
    }
}
