package com.javaflow.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BcdUtils {

    /**
     * Example:
     * <ul>
     * <li>10 -> 0x10</li>
     * <li>16 -> 0x16</li>
     * <li>99 -> 0x99</li>
     * </ul>
     */
    public byte[] decimalToBcd(long num) {
        if (num < 0) {
            throw new IllegalArgumentException(
                    String.format("The method decimalToBcd doesn't support negative numbers. Invalid argument: %d",
                            num));
        }
        if (num == 0) {
            return new byte[]{(byte) 0x00};
        }

        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;

        byte[] bcd = new byte[byteLen];

        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i % 2 == 0) {
                bcd[i / 2] = tmp;
            } else {
                bcd[i / 2] |= (byte) (tmp << 4);
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        return bcd;
    }

    public byte smallDecimalToBcd(int num) {
        if (num < 0 || num > 99) {
            throw new IllegalArgumentException(
                    String.format("The method smallDecimalToBcd only support 0~99. Invalid argument: %d", num));
        }
        return decimalToBcd(num)[0];
    }

    /**
     * Example:
     * <ul>
     * <li>0x10 -> 10</li>
     * <li>0x16 -> 16</li>
     * <li>0x99 -> 99</li>
     * </ul>
     */
    public long bcdToDecimal(byte[] bcd) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bcd) {
            byte high = (byte) (b & 0xf0);
            high >>>= (byte) 4;
            high = (byte) (high & 0x0f);
            byte low = (byte) (b & 0x0f);
            sb.append(high).append(low);
        }
        return Long.parseLong(sb.toString());
    }

    /**
     * Example:
     * <ul>
     * <li>0x10 -> 10</li>
     * <li>0x16 -> 16</li>
     * <li>0x99 -> 99</li>
     * </ul>
     */
    public int bcdToDecimal(byte bcd) {
        return (int) bcdToDecimal(new byte[]{bcd});
    }

}
