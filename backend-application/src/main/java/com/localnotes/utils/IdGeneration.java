package com.localnotes.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class IdGeneration {

    private static final Random random = new Random();
    private static final int leftLimit = 48;
    private static final int rightLimit = 122;
    private static final int targetStringLength = 10;

    private IdGeneration() {

    }

    public static String createId() {

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
