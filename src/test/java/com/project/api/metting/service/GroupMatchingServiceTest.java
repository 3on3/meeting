package com.project.api.metting.service;

import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import com.project.api.metting.dto.response.GroupResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GroupMatchingServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GroupMatchingServiceTest.class);
    @Autowired
    private GroupMatchingService groupMatchingService;

    @Test
    @DisplayName("그룹 히스토리 생성 테스트")
    void createGroupMatchingHistories() {
        //given
        String RequestGroupId = "9db3742d-2384-4c43-9171-1d81e92a0b8e";
        String ResponseGroupId = "204675bf-9390-4da3-888e-b15be82fd8f0";
//        String ResponseGroupId = "8840bec2-7a19-4611-a17e-c08691430ab0";
//        String RequestGroupId = "1fc3a005-f582-4f44-9b54-410aa1a4b952";


        //when
//        Grou
        //then
//        groupMatchingService.createHistory(build);

    }

    @Test
    @DisplayName("주최자 기준 신청자리스트 반환")
    void viewRequestList() {
        //given
        String groupId = "8840bec2-7a19-4611-a17e-c08691430ab0";
        //when
//        List<GroupResponseDto> groupResponseDtoList = groupMatchingService.viewRequestList(groupId);

        //then
//        groupResponseDtoList.forEach(System.out::println);

    }
//    @Test
//    @DisplayName("그룹 히스토리 프로세싱 함수")
//    void processGroupMatchingHistories() {
//        //given
//        String historyId = "fe771b63-e666-4750-99c2-3a18b13677f9";
//        //when
//
//        //then
////        groupMatchingService.processingHistory(historyId);
//
//    }

//    @Test
//    @DisplayName("")
//    void acceptRequestTest() {
//        //given
//            String historyId = "fe771b63-e666-4750-99c2-3a18b13677f9";
//        //when
//        groupMatchingService.acceptRequest(historyId);
//        //then
//
//    }
}