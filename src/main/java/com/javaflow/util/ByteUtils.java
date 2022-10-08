package com.javaflow.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.StringJoiner;

@UtilityClass
public class ByteUtils {

    public static String toHexString(byte[] bytes) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (byte b : bytes) {
            stringJoiner.add(toHexString(b));
        }
        return "[" + stringJoiner + "]";
    }

    public static String toHexString(byte aByte) {
        return String.format("%02X", aByte);
    }

    public static byte[] fill(Integer length, byte aByte) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = aByte;
        }
        return bytes;
    }

    public static boolean startWith(byte[] data, byte... startWith) {
        return Arrays.equals(startWith, 0, startWith.length, data, 0, startWith.length);
    }

    public static boolean endWith(byte[] data, byte... endWith) {
        return Arrays.equals(endWith, 0, endWith.length, data, data.length - endWith.length, data.length);
    }

}
