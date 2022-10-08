package com.javaflow.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtils {

    private static final Random RANDOM = new Random();

    public int nextInt(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }

    public int nextInt(int max) {
        return RANDOM.nextInt(max);
    }

}
