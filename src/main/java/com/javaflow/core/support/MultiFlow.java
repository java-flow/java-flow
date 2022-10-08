package com.javaflow.core.support;

import com.javaflow.core.exception.NodeDeployException;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.function.UnaryOperator;

public class MultiFlow extends Flow {

    @Getter
    private final List<Flow> flows = Lists.newArrayList();

    public MultiFlow(String title) {
        super(title);
    }

    public MultiFlow add(UnaryOperator<Flow> flowOperator) {
        Flow flow = new Flow(getTitle());
        flows.add(flowOperator.apply(flow));
        return this;
    }

    @Override
    public void deploy() throws NodeDeployException {
        for (Flow flow : flows) {
            flow.deploy();
        }
    }

    @Override
    public void destroy() {
        for (Flow flow : flows) {
            flow.destroy();
        }
    }

}
