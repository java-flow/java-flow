package com.javaflow.core.matcher;

import com.javaflow.util.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class ByteArrayStartWithMatchHandler<T> implements MatchHandler<byte[], T> {

    private final byte[] startWith;

    private final Function<byte[], T> handler;

    @Override
    public boolean match(byte[] data) {
        return ByteUtils.startWith(data, startWith);
    }

    @Override
    public T handle(byte[] data) {
        return handler.apply(data);
    }

}
