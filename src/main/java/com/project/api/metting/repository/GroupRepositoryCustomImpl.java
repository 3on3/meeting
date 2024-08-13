package com.project.api.metting.repository;

import com.project.api.metting.dto.request.GroupRequestDto;
import com.project.api.metting.dto.request.MainMeetingListFilterDto;
import com.project.api.metting.dto.request.MyChatListRequestDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.api.metting.entity.QGroup.group;


@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {
    private final JPAQueryFactory factory;
//    private final GroupUsersRepository groupUsersRepository;
//    private final GroupRepository groupRepository;


    //    main meetingList DTO
    @Override
    public Page<MainMeetingListResponseDto> findGroupUsersByAllGroup(String email, Pageable pageable) {

        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        // 공통 필터 조건
        BooleanExpression conditions = groupUser.auth.eq(GroupAuth.HOST)
                .and(group.isMatched.eq(false));

        BooleanExpression registeredUserCountCondition = JPAExpressions
                .select(groupUser.count().intValue())
                .from(groupUser)
                .where(groupUser.group.eq(group)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED)))
                .eq(group.maxNum);


        // 조건 결합
        BooleanExpression combinedCondition = conditions.and(registeredUserCountCondition);


        // 그룹 필터링 및 페이징
        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .orderBy(group.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 계산
        Long count = factory.select(group.count())
                .from(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .groupBy(group.id)
                .stream().count();

        List<MainMeetingListResponseDto> meetingList = groups.stream()
                .map(this::convertToMeetingListDto)
                .collect(Collectors.toList());

        // ================= setExistMatchingHistory
//        // 해당 사용자가 속한 그룹들을 가져옴
//        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);
//        // groupsByUserEmail 리스트의 ID들을 Set으로 변환
//        Set<String> userGroupIds = groupsByUserEmail.stream()
//                .map(Group::getId)
//                .collect(Collectors.toSet());
//        // groupUsersByAllGroup 리스트를 순회하며 ID를 비교하여 isExistMatchingHistory 설정
//        for (MainMeetingListResponseDto dto : meetingList) {
//            if (userGroupIds.contains(dto.getId())) {
//                dto.setExistMatchingHistory(true);
//            }
//        }
        return new PageImpl<>(meetingList, pageable, count);

    }

    //    main meetingList DTO 필러링
    @Override
    public Page<MainMeetingListResponseDto> filterGroupUsersByAllGroup(MainMeetingListFilterDto dto) {

        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        // 공통 필터 조건
        BooleanExpression conditions = groupUser.auth.eq(GroupAuth.HOST)
                .and(containGender(dto.getGender()))
                .and(containPlace(dto.getGroupPlace()))
                .and(containmaxNum(dto.getMaxNum()))
                .and(group.isMatched.eq(false));

        BooleanExpression registeredUserCountCondition = JPAExpressions
                .select(groupUser.count().intValue())
                .from(groupUser)
                .where(groupUser.group.eq(group)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED)))
                .eq(group.maxNum);


        // 조건 결합
        BooleanExpression combinedCondition = conditions.and(registeredUserCountCondition);

        // pageable 계산
        Pageable pageable = PageRequest.of(dto.getPageNo() - 1, dto.getPageSize());

        // 그룹 필터링 및 페이징
        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .orderBy(group.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 계산
        Long count = factory.select(group.count())
                .from(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .groupBy(group.id)
                .stream().count();

        List<MainMeetingListResponseDto> meetingList = groups.stream()
                .map(this::convertToMeetingListDto)
                .collect(Collectors.toList());

//        List<Group> groupsByUserEmail = groupRepository.findGroupsEntityByUserEmail(email);
//
//        meetingList.forEach(mainMeetingListResponseDto ->
//                mainMeetingListResponseDto.getId() == groupsByUserEmail.forEach(group1 -> group1.getId()));
//        groupsByUserEmail.forEach(group1 -> existsGroupMatchingHistoryByResponse(group1));

        return new PageImpl<>(meetingList, pageable, count);
    }


    // main meetingList DTO
    public MainMeetingListResponseDto convertToMeetingListDto(Group group) {
        return new MainMeetingListResponseDto(group, calculateAverageAge(group), hostMajor(group));
    }


    //    main filter :
//    성별 필터링 : 없으면 null로 반환
    private BooleanExpression containGender(String gender) {
        return StringUtils.hasText(gender) ? group.groupGender.eq(Gender.valueOf(gender)) : null;
    }

    //    인원수 필터링 : 없으면 null로 반환
    private BooleanExpression containmaxNum(Integer maxNum) {
        return maxNum != null ? group.maxNum.eq(maxNum) : null;
    }

    //    장소 필터링 : 없으면 null로 반환
    private BooleanExpression containPlace(String place) {
        return StringUtils.hasText(place) ? group.groupPlace.eq(Place.valueOf(place)) : null;
    }

    //    매칭 가능한 필터링 : 없으면 null로 반환
    private BooleanExpression containIsMatched(Boolean isMatched) {

        if (isMatched == null || isMatched) {
            return null; // 필터링을 적용하지 않음 (전체 반환)
        }
        return group.isMatched.eq(false);
    }


    //GroupResponseDto - 마이페이지 내가 속한 그룹
    @Override
    public List<GroupResponseDto> findGroupsByUserEmail(String email) {
        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(groupUser.user.email.eq(email)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED))) // status 필터링 추가)
                .fetch();

        return groups.stream().map(this::convertToGroupResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<Group> findGroupsEntityByUserEmail(String email) {
        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(groupUser.user.email.eq(email)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED))) // status 필터링 추가)
                .fetch();

        return groups;
    }

    //    GroupResponseDto
    public GroupResponseDto convertToGroupResponseDto(Group group) {
        int memberCount = (int) group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .count();

        return new GroupResponseDto(group, memberCount, calculateAverageAge(group), hostMajor(group));
    }

    //    GroupResponseDto
    public GroupRequestDto convertToGroupRequestDto(Group group) {
        int memberCount = group.getGroupUsers().size();


        return new GroupRequestDto(group, memberCount, calculateAverageAge(group), hostMajor(group));
    }


    // 여기야
    @Override
    public Integer myChatListRequestDto(Group group) {
        return calculateAverageAge(group);
    }


    //  Date 생년월일을 나이로 변경
    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;

        LocalDate birthLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears() + 2;
    }

    //    평균 나이 계산
    public int calculateAverageAge(Group group) {
        return (int) Math.round(group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .mapToDouble(gr -> calculateAge(gr.getUser().getBirthDate()))
                .average().orElse(0));
    }

    //    호스트 전공 추출
    private String hostMajor(Group group) {
        return group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getAuth() == GroupAuth.HOST)
                .map(groupUser -> groupUser.getUser().getMajor())
                .findFirst()
                .orElse("Unknown");
    }


    //  group 히스토리 조회
    public boolean existsGroupMatchingHistoryByResponse(Group group) {
        QGroup qGroup = QGroup.group;
        QGroupMatchingHistory qGroupMatchingHistory = QGroupMatchingHistory.groupMatchingHistory;

        // 결과가 존재하는지 유무 체크
        return factory.selectOne()
                .from(qGroup)
                .join(qGroupMatchingHistory)
                .on(qGroup.eq(qGroupMatchingHistory.responseGroup))
                .where(qGroup.eq(group))  // group 객체를 직접 비교
                .fetchFirst() != null;
    }

}