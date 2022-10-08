package com.javaflow.core.matcher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class ByteArrayContainsMatchHandler<T> implements MatchHandler<byte[], T> {

    private final byte[] contains;

    private final Integer fromIndex;

    private final Function<byte[], T> handler;

    @Override
    public boolean match(byte[] data) {
        if (data.length < fromIndex + contains.length) {
            return false;
        }
        return Arrays.equals(contains, 0, contains.length, data, fromIndex, fromIndex + contains.length);
    }

    @Override
    public T handle(byte[] data) {
        return handler.apply(data);
    }

}
