package com.project.api.metting.repository;

import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.QUser;
import com.project.api.metting.entity.QUserMembership;
import com.project.api.metting.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@RequiredArgsConstructor
@Slf4j

public class UserMembershipRepositoryCustomImpl implements UserMembershipRepositoryCustom{
    private final JPAQueryFactory factory;
//    @Override
//    public Membership membershipAuthFind(String userEmail) {
//
//        QUserMembership qUserMembership=QUserMembership.userMembership;
//        QUser qUser = QUser.user;
//
//
//        return factory.select(qUserMembership.auth)
//                .join(qUserMembership.user, qUser)
//                .where(qUser.email.eq(userEmail))
//                .fetchOne();
//    }
}
