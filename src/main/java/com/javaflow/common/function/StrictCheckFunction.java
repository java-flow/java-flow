package com.javaflow.common.function;

import com.javaflow.core.constant.MsgKeyConstant;
import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
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
public class StrictCheckFunction extends Node {

    private final Boolean strictCheckEnable;

    @Override
    public Msg invoke(Msg msg) {
        if (Boolean.TRUE.equals(strictCheckEnable) && Boolean.TRUE.equals(msg.error())) {
            log.error("{} error detected, set msg forbidden", getLogTitle());
            msg.put(MsgKeyConstant.FORBIDDEN, true);
        }
        return super.invoke(msg);
    }

}
