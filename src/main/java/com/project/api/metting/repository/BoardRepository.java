package com.project.api.metting.repository;

import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByAuthor(User user);
    List<Board> findByIsDeletedFalseOrderByCreatedAtDesc();
    List<Board> findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(User author);

}
