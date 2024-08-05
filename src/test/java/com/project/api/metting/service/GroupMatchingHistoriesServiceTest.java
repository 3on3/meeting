package com.project.api.metting.service;

import com.project.api.metting.dto.request.GroupMatchingHistoryRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupMatchingHistoriesServiceTest {

    @Autowired
    private GroupMatchingHistoriesService groupMatchingHistoriesService;

    @Test
    @DisplayName("그룹 히스토리 생성 테스트")
    void createGroupMatchingHistories() {
        //given
        String RequestGroupId = "251d4149-24a8-4780-b990-fa4a079dee12";
        String ResponseGroupId = "6d0d7647-c613-4ab2-82c1-ec2849f7b905";


        //when
        GroupMatchingHistoryRequestDto build = GroupMatchingHistoryRequestDto.builder()
                .requestGroupId(RequestGroupId)
                .responseGroupId(ResponseGroupId)
                .build();
        //then
        groupMatchingHistoriesService.createHistory(build);

    }

    @Test
    @DisplayName("그룹 히스토리 프로세싱 함수")
    void processGroupMatchingHistories() {
        //given
        String historyId = "1d82dee9-b7fb-4dbe-9bac-881faeb3a3c1";
        //when

        //then
        groupMatchingHistoriesService.processingHistory(historyId);

    }
}