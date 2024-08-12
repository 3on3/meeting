package com.project.api.metting.service;

import com.project.api.metting.entity.Gender;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserSignUpServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterUsers() {
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .email("testuser" + i + "@example.com")
                    .password("password" + i)
                    .name("Test User " + i)
                    .birthDate(new Date())
                    .phoneNumber("123-456-789" + i)
                    .univName("University " + i)
                    .major("Major " + i)
                    .gender(Gender.F)  // Gender.FEMALE로 수정
                    .nickname("Nickname " + i)
                    .build();

            userRepository.save(user);
        }

        // Verify that users have been saved
        assertEquals(10, userRepository.count(), "User count should be 10");
    }
}