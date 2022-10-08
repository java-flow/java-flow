package com.javaflow.core.matcher;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

@Slf4j
@UtilityClass
public class MatchHandlers {

    public MatchHandler<String, Object> regex(String pattern, Function<Matcher, Object> handler) {
        return new RegexMatchHandler(pattern, handler);
    }

    public <T> MatchHandler<byte[], T> byteArrayStartWith(byte[] startWith, Function<byte[], T> handler) {
        return new ByteArrayStartWithMatchHandler<>(startWith, handler);
    }

    public <T> MatchHandler<byte[], T> byteArrayContains(byte[] contains, int fromIndex, Function<byte[], T> handler) {
        return new ByteArrayContainsMatchHandler<>(contains, fromIndex, handler);
    }

    public <R, T> boolean match(List<MatchHandler<R, T>> matchHandlers, R r) {
        return matchHandlers.stream()
                .anyMatch(matchHandler -> matchHandler.match(r));
    }

    public <R, T> T handle(List<MatchHandler<R, T>> matchHandlers, R r) {
        MatchHandler<R, T> handler = matchHandlers.stream()
                .filter(matchHandler -> matchHandler.match(r))
                .findFirst()
                .orElse(null);
        if (handler == null) {
            log.warn("Not found handler match: {}", r);
            return null;
        }
        return handler.handle(r);
    }

}
