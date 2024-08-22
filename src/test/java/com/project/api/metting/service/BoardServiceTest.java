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
        User user = userRepository.findById("faa46119-537f-4c00-b34a-996afa8190af").orElseThrow();
        System.out.println("user = " + user);
        for (int i = 0; i < 11; i++) {
            boardRepository.save(Board.builder().title("제목"+i).content("내용내욘앤요냉 애뇨앤애뇬 앤욜내앨ㄴ").author(user).build());
        }
        //when

        //then
    }
}