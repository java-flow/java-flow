package com.javaflow.core.support;

import com.javaflow.core.exception.NodeDeployException;

public interface FlowDefiner {

    Flow define();

    default Flow deploy() throws NodeDeployException {
        Flow flow = define();
        flow.deploy();
        return flow;
    }

}
