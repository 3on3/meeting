package com.project.api.metting.service;

import com.project.api.metting.entity.*;
import com.project.api.metting.repository.UserMembershipRepository;
import com.project.api.metting.repository.UserProfileRepository;
import com.project.api.metting.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserSignUpServiceTest {

    @Autowired
    private UserMembershipRepository UserMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserMembershipRepository userMembershipRepository;

    @Test
    public void testRegisterUsers() {
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .email("test" + i + "@example.com")
                    .password("pa" + i)
                    .name("Test User " + i)
                    .birthDate(new Date())
                    .phoneNumber("123-456-789" + i)
                    .univName("University " + i)
                    .major("Major " + i)
                    .gender(Gender.F)  // Gender.FEMALE로 수정
                    .nickname("Nickname " + i)
                    .build();

            String password = user.getPassword();
            String encodedPassword = encoder.encode(password);
            user.changePass(encodedPassword);
            userRepository.save(user);

            UserProfile profile = UserProfile.builder()
                    .profileImg("https://spring-file-bucket-yocong.s3.ap-northeast-2.amazonaws.com/2024/08/20/cd309cf8-3f33-4d6d-b0cc-6ac0f7a6593f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-08-09%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.27.24.png")
                    .user(user)
                    .profileIntroduce("")
                    .build();


            userProfileRepository.save(profile);

            UserMembership userMembership = UserMembership.builder()
                    .user(user)
                    .auth(Membership.GENERAL)
                    .build();

            userMembershipRepository.save(userMembership);
        }

        // Verify that users have been saved
        assertEquals(10, userRepository.count(), "User count should be 10");
    }
}