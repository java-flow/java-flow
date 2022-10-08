package com.javaflow.common.function;

import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import com.javaflow.util.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class RandomDelayFunction extends Node {

    private final Integer minDelayMs;

    private final Integer maxDelayMs;

    private Integer randomDelayPercent = 100;

    @SneakyThrows
    @Override
    public Msg invoke(Msg msg) {
        if (RandomUtils.nextInt(100) <= randomDelayPercent) {
            int delayMs = RandomUtils.nextInt(minDelayMs, maxDelayMs);
            log.info("{} random delay: {} ms", getLogTitle(), delayMs);
            Thread.sleep(delayMs);
        }
        return super.invoke(msg);
    }

}
