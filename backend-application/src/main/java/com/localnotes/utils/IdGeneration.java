package com.localnotes.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneration {

    private static final Random RANDOM = new Random();
    private static final int LEFT_LIMIT = 48;
    private static final int RIGHT_LIMIT = 122;
    private static final int TARGET_STRING_LENGTH = 10;

    public static String createId() {

        return RANDOM.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
