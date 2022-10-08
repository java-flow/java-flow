package com.javaflow.core.support;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Emitter {

    public static final String EMIT_EVENT_ARGS = "{} emit event: {} args: {}";

    public static final String LISTEN_EVENT = "{} listen event: {}";

    public static final String CANCEL_LISTEN_EVENT = "{} cancel listen event: {}";

    private final String source;

    private final Multimap<String, Callback> eventToCallbackMap = MultimapBuilder.hashKeys().hashSetValues().build();

    public Emitter() {
        this("Unknown source");
    }

    public Emitter(String source) {
        this.source = source;
    }

    public void emit(String event, Object... args) {
        log.info(EMIT_EVENT_ARGS, source, event, args);
        eventToCallbackMap.get(event)
                .forEach(callback -> callback.accept(args));
    }

    public void emit(Enum<?> event, Object... args) {
        log.info(EMIT_EVENT_ARGS, source, event, args);
        eventToCallbackMap.get(event.name())
                .forEach(callback -> callback.accept(args));
    }

    public void on(String event, Callback callback) {
        log.info(LISTEN_EVENT, source, event);
        eventToCallbackMap.put(event, callback);
    }

    public void on(Enum<?> event, Callback callback) {
        log.info(LISTEN_EVENT, source, event);
        eventToCallbackMap.put(event.name(), callback);
    }

    public void off(String event) {
        log.info(CANCEL_LISTEN_EVENT, source, event);
        eventToCallbackMap.removeAll(event);
    }

    public void off(Enum<?> event) {
        log.info(CANCEL_LISTEN_EVENT, source, event);
        eventToCallbackMap.removeAll(event.name());
    }

    public void off(String event, Callback callback) {
        log.info(CANCEL_LISTEN_EVENT, source, event);
        eventToCallbackMap.remove(event, callback);
    }

    public void off(Enum<?> event, Callback callback) {
        log.info(CANCEL_LISTEN_EVENT, source, event);
        eventToCallbackMap.remove(event.name(), callback);
    }

    public void clear() {
        eventToCallbackMap.clear();
    }

    @FunctionalInterface
    public interface Callback {

        void accept(Object... args);

    }

}
