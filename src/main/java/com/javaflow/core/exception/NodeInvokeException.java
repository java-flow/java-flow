package com.javaflow.core.exception;

import com.javaflow.core.support.Node;
import lombok.Getter;

@Getter
public class NodeInvokeException extends RuntimeException {

    private final transient Node node;

    public NodeInvokeException(Node node, String message) {
        super(String.format("%s invoke error: %s", node.getLogTitle(), message));
        this.node = node;
    }

}