package com.project.api.metting.service;

import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.BoardRepository;
import com.project.api.metting.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("dd")
    void crate() {
        //given
        User user = userRepository.findById("a8c329a4-7fa4-4dc2-9b91-c6ce0e11cfd2").orElseThrow();
        System.out.println("user = " + user);
        for (int i = 0; i < 11; i++) {
            boardRepository.save(Board.builder().title("제목"+i).content("내용내욘앤요냉 애뇨앤애뇬 앤욜내앨ㄴ").author(user).build());
        }
        //when

        //then
    }
}