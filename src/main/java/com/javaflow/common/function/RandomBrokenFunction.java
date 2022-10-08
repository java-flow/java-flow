package com.javaflow.common.function;

import com.javaflow.core.exception.NodeInvokeException;
import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import com.javaflow.util.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class RandomBrokenFunction extends Node {

    private final Integer minBrokenMs;

    private final Integer maxBrokenMs;

    private final Integer randomBrokenPercent;

    @Override
    public Msg invoke(Msg msg) {
        if (RandomUtils.nextInt(100) <= randomBrokenPercent) {
            throw new NodeInvokeException(this, "Random broken");
        }
        return super.invoke(msg);
    }

}
