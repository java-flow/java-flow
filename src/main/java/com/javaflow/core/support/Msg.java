package com.javaflow.core.support;

import com.javaflow.core.exception.MsgException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Optional;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class Msg extends HashMap<String, Object> {

    private transient Object payload;

    private Boolean error;

    public <T> T get(String key, @NonNull Class<T> tClass) {
        return Optional.ofNullable(get(key))
                .filter(tClass::isInstance)
                .map(tClass::cast)
                .orElseThrow(() -> new MsgException(String.format("Msg key [%s] with class [%s] not exist",
                        key,
                        tClass.getSimpleName())));
    }

    public <T> T getOrDefault(String key, @NonNull Class<T> tClass, T defaultValue) {
        return Optional.ofNullable(get(key))
                .filter(tClass::isInstance)
                .map(tClass::cast)
                .orElse(defaultValue);
    }

    public <T> T payloadAs(@NonNull Class<T> tClass) {
        if (tClass == String.class) {
            return tClass.cast(payloadAsString());
        }
        return Optional.ofNullable(payload)
                .filter(tClass::isInstance)
                .map(tClass::cast)
                .orElseThrow(() -> new MsgException(String.format("Msg payload with class [%s] not exist",
                        tClass.getSimpleName())));
    }

    public String payloadAsString() {
        if (payload == null) {
            throw new MsgException("Msg payload is null");
        }
        return payload.toString();
    }

}
