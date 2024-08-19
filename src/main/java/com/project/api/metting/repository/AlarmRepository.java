package com.project.api.metting.repository;

import com.project.api.metting.entity.Alarm;
import com.project.api.metting.entity.AlarmStatus;
import com.project.api.metting.entity.GroupMatchingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, String> {

    List<Alarm> findByGroupMatchingHistoryAndStatus(GroupMatchingHistory groupMatchingHistory, AlarmStatus status);
}
