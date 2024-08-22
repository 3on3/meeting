package com.project.api.metting.repository;

import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByAuthor(User user);
    Page<Board> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Board> findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable, User author);

}
