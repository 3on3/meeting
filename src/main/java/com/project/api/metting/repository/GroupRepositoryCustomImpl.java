package com.project.api.metting.repository;

import com.project.api.metting.dto.request.GroupRequestDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import com.project.api.metting.dto.response.MainMeetingListResponseDto;
import com.project.api.metting.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

import static com.project.api.metting.entity.QGroup.group;


@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {
    private final JPAQueryFactory factory;


    /**
     * 필터 조건에 따라 그룹 리스트를 페이징 처리하여 조회
     *
     * @param pageable  페이징 정보
     * @param gender    필터링할 성별 (선택 사항)
     * @param region    필터링할 지역 (선택 사항)
     * @param personnel 필터링할 인원수 (선택 사항)
     * @param email     사용자의 이메일
     * @return 필터링된 미팅 리스트의 페이지
     */
    @Override
    public Page<MainMeetingListResponseDto> findGroupUsersByAllGroup(Pageable pageable,String gender,String region,Integer personnel,String email) {

        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;
        QGroupMatchingHistory matchingHistory = QGroupMatchingHistory.groupMatchingHistory;




        // 그룹 히스토리가 MATCHED 인 ResponseGroup
        JPQLQuery<String> matchedResponseGroup = JPAExpressions
                .select(matchingHistory.responseGroup.id)
                .from(matchingHistory)
                .where(matchingHistory.process.eq(GroupProcess.MATCHED));

        // 그룹 히스토리가 MATCHED 인 RequestGroup
        JPQLQuery<String> matchedRequestGroup = JPAExpressions
                .select(matchingHistory.requestGroup.id)
                .from(matchingHistory)
                .where(matchingHistory.process.eq(GroupProcess.MATCHED));

        //그룹유저가 호스트이고, 내가 속한 그룹이 아닌 리스트만 반환
        //매칭이 된다면 리스트에서 제거
        // 성별, 지역, 인원수는 선택사항
        BooleanExpression conditions = groupUser.auth.eq(GroupAuth.HOST)
                .and(containGender(gender))
                .and(containPlace(region))
                .and(containmaxNum(personnel))
//                .and(group.isMatched.eq(false))
                .and(group.id.notIn(matchedResponseGroup))
                .and(group.id.notIn(matchedRequestGroup))
                .and(group.id.notIn(JPAExpressions
                        .select(groupUser.group.id)
                        .from(groupUser)
                        .where(groupUser.user.email.eq(email))));

        // 그룹의 인원 수가 최대 인원과 일치하는지 확인하는 조건.
        BooleanExpression registeredUserCountCondition = JPAExpressions
                .select(groupUser.count().intValue())
                .from(groupUser)
                .where(groupUser.group.eq(group)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED)))
                .eq(group.maxNum);




        // 필터 조건과 인원 수 조건을 결합
        BooleanExpression combinedCondition = conditions.and(registeredUserCountCondition);


        // 필터링된 그룹들을 페이징 처리
        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .orderBy(group.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //  페이징 처리를 위한 총 개수를 계산
        Long count = factory.select(group.count())
                .from(group)
                .join(group.groupUsers, groupUser)
                .where(combinedCondition)
                .groupBy(group.id)
                .stream().count();

        // Group 엔티티를 DTO로 변환
        List<MainMeetingListResponseDto> meetingList = groups.stream()
                .map(this::convertToMeetingListDto)
                .collect(Collectors.toList());

        // 페이징 처리된 결과를 반환
        return new PageImpl<>(meetingList, pageable, count);

    }


    /**
     * Group 엔티티를 MainMeetingListResponseDto로 변환
     *
     * @param group 그룹 엔티티
     * @return 그룹 정보를 담은 DTO
     */
    public MainMeetingListResponseDto convertToMeetingListDto(Group group) {
        return new MainMeetingListResponseDto(group, calculateAverageAge(group), hostMajor(group));
    }


    /**
     * 성별 필터링 조건 생성
     *
     * @param gender 필터링할 성별
     * @return 성별 필터링 조건 (없으면 null)
     */
    private BooleanExpression containGender(String gender) {
        return StringUtils.hasText(gender) ? group.groupGender.eq(Gender.valueOf(gender)) : null;
    }

    /**
     * 인원수 필터링 조건 생성
     *
     * @param maxNum 필터링할 최대 인원수
     * @return 인원수 필터링 조건 (없으면 null)
     */
    private BooleanExpression containmaxNum(Integer maxNum) {
        return maxNum != null ? group.maxNum.eq(maxNum) : null;
    }

    /**
     * 장소 필터링 조건 생성
     *
     * @param place 필터링할 장소
     * @return 장소 필터링 조건 (없으면 null)
     */
    private BooleanExpression containPlace(String place) {
        return StringUtils.hasText(place) ? group.groupPlace.eq(Place.valueOf(place)) : null;
    }

    /**
     * 매칭 가능 여부 필터링 조건 생성
     *
     * @param isMatched 매칭 여부
     * @return 매칭 가능 여부 필터링 조건 (없으면 null)
     */
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

    /**
     * 유저가 호스트로 권한으로 속한 그룹
     * @param email - 유저 이메일
     * @return - 그룹 dto
     */
    @Override
    public List<GroupResponseDto> findGroupsByUserIdAndUserAuthHost(String email) {
        QGroup group = QGroup.group;
        QGroupUser groupUser = QGroupUser.groupUser;

        List<Group> groups = factory.selectFrom(group)
                .join(group.groupUsers, groupUser)
                .where(groupUser.user.email.eq(email)
                        .and(groupUser.status.eq(GroupStatus.REGISTERED))
                        .and(groupUser.auth.eq(GroupAuth.HOST))) // status 필터링 추가)
                .fetch();

        return groups.stream().map(this::convertToGroupResponseDto).collect(Collectors.toList());
    }


    /**
     * 유저가 속해있는 그룹 조회
     * @param email - 유저의 이메일
     * @return - 유저가 속한 그룹 리스트
     */
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

    /**
     * Group 엔티티를 GroupResponseDto로 변환
     *
     * @param group 그룹 엔티티
     * @return 그룹 정보를 담은 DTO
     */
    public GroupResponseDto convertToGroupResponseDto(Group group) {
        int memberCount = (int) group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .count();

        return new GroupResponseDto(group, memberCount, calculateAverageAge(group), hostMajor(group));
    }

    /**
     * Group 엔티티를 GroupRequestDto로 변환
     *
     * @param group 그룹 엔티티
     * @return 그룹 정보를 담은 요청 DTO
     */
    public GroupRequestDto convertToGroupRequestDto(Group group) {
        int memberCount = group.getGroupUsers().size();


        return new GroupRequestDto(group, memberCount, calculateAverageAge(group), hostMajor(group));
    }


    @Override
    public Integer myChatListRequestDto(Group group) {
        return calculateAverageAge(group);
    }


    /**
     * 생년월일을 나이로 변환
     *
     * @param birthDate 생년월일
     * @return 나이
     */
    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;

        LocalDate birthLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears() + 2;
    }

    /**
     * 그룹의 평균 나이를 계산
     *
     * @param group 그룹 엔티티
     * @return 평균 나이
     */
    public int calculateAverageAge(Group group) {
        return (int) Math.round(group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus() == GroupStatus.REGISTERED)
                .mapToDouble(gr -> calculateAge(gr.getUser().getBirthDate()))
                .average().orElse(0));
    }

    /**
     * 그룹의 호스트 전공 추출
     *
     * @param group 그룹 엔티티
     * @return 호스트의 전공
     */
    private String hostMajor(Group group) {
        return group.getGroupUsers().stream()
                .filter(groupUser -> groupUser.getAuth() == GroupAuth.HOST)
                .map(groupUser -> groupUser.getUser().getMajor())
                .findFirst()
                .orElse("Unknown");
    }


}