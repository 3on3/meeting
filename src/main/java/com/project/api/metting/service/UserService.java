package com.project.api.metting.service;


import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User findUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
