package com.javaflow.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

    public static String format(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof byte[]) {
            return ByteUtils.toHexString((byte[]) object);
        }
        return object.toString();
    }

}
