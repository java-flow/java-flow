package com.javaflow.core.exception;

import com.javaflow.core.support.Node;
import lombok.Getter;

@Getter
public class NodeDeployException extends RuntimeException {

    private final transient Node node;

    public NodeDeployException(Node node, String message) {
        super(String.format("%s deploy error: %s", node.getLogTitle(), message));
        this.node = node;
    }

    public NodeDeployException(Node node, String message, Throwable cause) {
        super(String.format("%s deploy error: %s", node.getLogTitle(), message), cause);
        this.node = node;
    }

}
