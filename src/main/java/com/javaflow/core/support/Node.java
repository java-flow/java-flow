package com.javaflow.core.support;

import com.javaflow.core.exception.NodeDeployException;
import lombok.Data;
import lombok.Getter;

@Data
public abstract class Node {

    private String title;

    private Flow flow;

    @Getter(lazy = true)
    private final String logTitle = String.format("Flow [%s] Node [%s]", flow.getTitle(), getTitle());

    @Getter(lazy = true)
    private final Emitter emitter = new Emitter(getLogTitle());

    public Msg invoke(Msg msg) {
        return msg;
    }

    public void onDeploy() throws NodeDeployException {
    }

    public void onDestroy() {
    }

    public String getTitle() {
        return title != null ? title : this.getClass().getSimpleName();
    }

}
