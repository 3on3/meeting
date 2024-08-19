package com.project.api.metting.service;

import com.project.api.metting.dto.request.AlarmListRequestDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GroupUsersRepository groupUsersRepository;
    private final GroupMatchingHistoriesRepository groupMatchingHistoriesRepository;
    private final UserProfileRepository userProfileRepository;

    public List<AlarmListRequestDto> findAlarmList(String userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {return null;}

        List<GroupUser> userGroup = groupUsersRepository.findByUserAndStatus(user, GroupStatus.REGISTERED);

        List<GroupMatchingHistory> groupMatchingHistories = new ArrayList<>();

        for (GroupUser groupUser : userGroup) {
            if(!groupUser.getGroup().getIsDeleted() && !groupUser.getGroup().getIsMatched() && groupUser.getAuth() == GroupAuth.HOST) {
                List<GroupMatchingHistory> groupMatchingHistoryList= groupMatchingHistoriesRepository.findAllByResponseGroupAndProcess(groupUser.getGroup(), GroupProcess.INVITING);
                groupMatchingHistories.addAll(groupMatchingHistoryList);
            }
        }

        if(groupMatchingHistories.isEmpty()) {return null;}

        List<Alarm> alarms = new ArrayList<>();

        for (GroupMatchingHistory groupMatchingHistory : groupMatchingHistories) {
            List<Alarm> alarmList = alarmRepository.findByGroupMatchingHistoryAndStatus(groupMatchingHistory, AlarmStatus.NOT_CHECKED);
            alarms.addAll(alarmList);
        }

        List<AlarmListRequestDto> alarmListRequestDtos = new ArrayList<>();

        for (Alarm alarm : alarms) {

            GroupMatchingHistory groupMatchingHistory = alarm.getGroupMatchingHistory();
            Group requestGroup = alarm.getGroupMatchingHistory().getRequestGroup();
            GroupUser requestHostUser = groupUsersRepository.findByGroupAndAuth(requestGroup, GroupAuth.HOST);

            UserProfile hostUserProfile = userProfileRepository.findByUser(requestHostUser.getUser());
            String imgUrl;

            if(hostUserProfile == null) {
                imgUrl = "";
            } else if (hostUserProfile.getProfileImg() == null) {
                imgUrl = "";
            } else {
                imgUrl = hostUserProfile.getProfileImg();
            }


            AlarmListRequestDto alarmListRequestDto = AlarmListRequestDto.builder()
                    .requestGroupName(requestGroup.getGroupName())
                    .requestGroupHostProfile(imgUrl)
                    .alarmId(alarm.getId())
                    .requestedAt(groupMatchingHistory.getRequestedAt())
                    .responseGroupId(groupMatchingHistory.getResponseGroup().getId())
                    .build();

            alarmListRequestDtos.add(alarmListRequestDto);
        }

        return alarmListRequestDtos;

    }
}
