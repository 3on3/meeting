package com.project.api.metting.util;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class RandomUtil {


    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateRandomCode(char from, char to, int length) {
        if (from > to || length < 1) {
            throw new IllegalArgumentException("Invalid range or length");
        }
        StringBuilder randomCode = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            randomCode.append(CHARACTERS.charAt(index));
        }
        return randomCode.toString();
    }
}
