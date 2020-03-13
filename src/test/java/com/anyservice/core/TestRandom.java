package com.anyservice.core;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class TestRandom {

    private static final SecureRandom random = new SecureRandom();

    public static int randomNumber(int min, int max) {
        return random
                .ints(min, (max + 1))
                .findFirst()
                .getAsInt();
    }

    public static boolean randomBoolean() {
        return randomNumber(0, 1) == 0;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
