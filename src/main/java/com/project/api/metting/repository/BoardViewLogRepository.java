package com.project.api.metting.repository;

import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.BoardViewLog;
import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardViewLogRepository extends JpaRepository<BoardViewLog, String> {
    BoardViewLog findByBoardAndUser(Board board, User user);
}
