package com.project.api.metting.repository;

import com.project.api.metting.entity.BoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReplyRepository extends JpaRepository<BoardReply, String> {
    Page<BoardReply> findByBoardIdAndIsDeletedFalse(Pageable pageable, String boardId);
}
