package com.project.api.metting.service;

import com.project.api.metting.dto.request.GroupMatchingRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GroupMatchingServiceTest {

    @Autowired
    private GroupMatchingService groupMatchingService;

    @Test
    @DisplayName("그룹 히스토리 생성 테스트")
    void createGroupMatchingHistories() {
        //given
        String RequestGroupId = "9b4e09cd-8c4c-4b94-bb95-36f54b9d0c6f";
        String ResponseGroupId = "fd463cad-bf42-4cfc-93f5-57dc9c5f7171";
//        String ResponseGroupId = "9b4e09cd-8c4c-4b94-bb95-36f54b9d0c6f";
//        String RequestGroupId = "fd463cad-bf42-4cfc-93f5-57dc9c5f7171";


        //when
        GroupMatchingRequestDto build = GroupMatchingRequestDto.builder()
                .requestGroupId(RequestGroupId)
                .responseGroupId(ResponseGroupId)
                .build();
        //then
        groupMatchingService.createHistory(build);

    }

    @Test
    @DisplayName("그룹 히스토리 프로세싱 함수")
    void processGroupMatchingHistories() {
        //given
        String historyId = "dda0fb92-3105-46d2-a4ba-fd4998a54051";
        //when

        //then
        groupMatchingService.processingHistory(historyId);

    }
}