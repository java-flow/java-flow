package com.javaflow.core.matcher;

public interface MatchHandler<T, R> {

    boolean match(T data);

    R handle(T data);

}
