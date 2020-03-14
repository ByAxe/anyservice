package com.anyservice.core;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class RandomValuesGenerator {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates random number in range
     *
     * @param min minimum size
     * @param max maximum size
     * @return random integer value
     */
    public static int randomNumber(int min, int max) {
        return random
                .ints(min, (max + 1))
                .findFirst()
                .getAsInt();
    }

    public static boolean randomBoolean() {
        return randomNumber(0, 1) == 0;
    }

    /**
     * Generates random Enum value for given Enum class
     * <p>
     * Example usage:
     * <code>
     * LegalStatus status = randomEnum(LegalStatus.class);
     * </code>
     * Where "status" gets a random value of possible set of values from "LegalStatus" enum
     *
     * @param clazz class of enum
     * @param <T>   Enum type
     * @return random value from enum
     */
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * Generates random string of random length in range
     *
     * @param min minimum size
     * @param max maximum size
     * @return random string of random size
     */
    public static String randomString(int min, int max) {
        int stringLength = randomNumber(min, max);

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Generates random string of given length
     *
     * @param stringLength length of a random string
     * @return Randomly generated string of given size
     */
    public static String randomString(int stringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
