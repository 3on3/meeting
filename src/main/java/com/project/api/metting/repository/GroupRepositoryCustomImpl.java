package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.entity.Group;
import com.project.api.metting.entity.QGroup;
import com.project.api.metting.entity.QGroupUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepositoryCustomImpl implements  GroupRepositoryCustom {
    private final JPAQueryFactory factory;

    @Override
    public List<GroupResponseDto> findGroupsByUserEmail(String email) {
        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(groupUser.user.email.eq(email))
                .fetch();

        return groups.stream().map(this::convertToGroupResponseDto).collect(Collectors.toList());
    }

    private GroupResponseDto convertToGroupResponseDto(Group group) {
        int memberCount = group.getGroupUsers().size();
        double averageAge = group.getGroupUsers().stream()
                .mapToDouble(gu -> calculateAge(gu.getUser().getBirthDate()))
                .average().orElse(0);

        return new GroupResponseDto(
                group.getId(),
                group.getGroupName(),
                group.getGroupPlace().toString(),
                memberCount,
                averageAge
        );
    }

    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;

        LocalDate birthLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }



}