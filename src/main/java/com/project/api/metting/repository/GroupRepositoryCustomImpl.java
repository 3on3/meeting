package com.project.api.metting.repository;

import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepositoryCustomImpl implements  GroupRepositoryCustom {
    private final JPAQueryFactory factory;


//    main meetingList DTO
    @Override
    public List<MainMeetingListResponseDto> findGroupUsersByAllGroup(){

        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(groupUser.auth.eq(GroupAuth.HOST))
                .fetch();

        return groups.stream().map(this::convertToMeetingListDto).collect(Collectors.toList());

    }

    // main meetingList DTO
    public MainMeetingListResponseDto convertToMeetingListDto(Group group){
        return new MainMeetingListResponseDto(group, calculateAverageAge(group),hostMajor(group));

    }


    //GroupResponseDto - 마이페이지 내가 속한 그룹
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

//    GroupResponseDto
    private GroupResponseDto convertToGroupResponseDto(Group group) {
        int memberCount = group.getGroupUsers().size();

        return new GroupResponseDto(group,memberCount,calculateAverageAge(group),hostMajor(group));
    }


//  Date 생년월일을 나이로 변경
    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;

        LocalDate birthLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }

    //    평균 나이 계산
    private int calculateAverageAge(Group group) {
        return (int) Math.round(group.getGroupUsers().stream()
                .mapToDouble(gr -> calculateAge(gr.getUser().getBirthDate()))
                .average().orElse(0));
    }

//    호스트 전공 추출
    private String hostMajor(Group group){
        return group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getAuth() == GroupAuth.HOST)
                .map(groupUser -> groupUser.getUser().getMajor())
                .findFirst()
                .orElse("Unknown");
    }

    ////        String 생년월일을 나이로 변경
//    private int calculateAge(String birthDate) {
//        if (birthDate == null) return 0;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
//        LocalDate birthLocalDate = LocalDate.parse(birthDate, formatter);
//        LocalDate now = LocalDate.now();
//        return Period.between(birthLocalDate, now).getYears();
//    }


//    필터링 커스텀







}