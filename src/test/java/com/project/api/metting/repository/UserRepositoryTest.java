package com.project.api.metting.repository;

import com.project.api.metting.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("asd")
    void gd() {
        //given

        User user = User.builder()
                .email("tkdgnsdldkdlel@gmail.com")
                .build();

        //when

        userRepository.save(user);


        //then
    }


}