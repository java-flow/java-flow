package com.javaflow.core.support;

import com.javaflow.core.exception.NodeDeployException;
import com.javaflow.util.LogUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;

@Slf4j
@Getter
@AllArgsConstructor
public class Flow {

    private final String title;

    private final LinkedList<Node> pipeline = Lists.newLinkedList();

    @Getter(lazy = true)
    private final Emitter emitter = new Emitter(String.format("Flow [%s]", getTitle()));

    public Flow next(Node node) {
        this.pipeline.add(node);
        node.setFlow(this);
        return this;
    }

    public Flow next(UnaryOperator<Msg> function) {
        Node node = new Node() {
            @Override
            public Msg invoke(Msg msg) {
                return function.apply(msg);
            }
        };
        node.setTitle(function.getClass().getSimpleName());
        return next(node);
    }

    public Msg invoke(Msg msg) {
        for (Node node : pipeline) {
            log.info("{} invoke msg payload: {}", node.getLogTitle(), LogUtils.format(msg.payload()));
            msg = node.invoke(msg);
        }
        return msg;
    }

    public Future<Msg> invokeAsync(Msg msg) {
        return Executors.newSingleThreadExecutor()
                .submit(() -> invoke(msg));
    }

    public void nextInvoke(Node node, Msg msg) {
        int nextNodeIndex = pipeline.indexOf(node) + 1;
        if (nextNodeIndex <= 0 || nextNodeIndex > pipeline.size()) {
            return;
        }
        ListIterator<Node> nodeListIterator = pipeline.listIterator(nextNodeIndex);
        while (nodeListIterator.hasNext()) {
            Node nextNode = nodeListIterator.next();
            log.info("{} invoke msg payload: {}", nextNode.getLogTitle(), LogUtils.format(msg.payload()));
            msg = nextNode.invoke(msg);
        }
    }

    public void deploy() throws NodeDeployException {
        List<Node> deployedNode = Lists.newArrayList();
        Exception deployException = null;
        Node deployExceptionNode = null;
        for (Node node : pipeline) {
            try {
                log.info("{} deploy", node.getLogTitle());
                node.onDeploy();
                deployedNode.add(node);
            } catch (Exception e) {
                log.error("{} deploy error: {}", node.getLogTitle(), e.getMessage(), e);
                deployException = e;
                deployExceptionNode = node;
                break;
            }
        }
        if (deployException != null) {
            log.error("Flow [{}] deploy failed", getTitle());
            for (Node node : Lists.reverse(deployedNode)) {
                try {
                    log.info("{} destroy", node.getLogTitle());
                    node.onDestroy();
                } catch (Exception e) {
                    log.warn("{} destroy error: {}", node.getLogTitle(), e.getMessage(), e);
                }
            }
            if (deployException instanceof NodeDeployException) {
                throw (NodeDeployException) deployException;
            } else {
                throw  new NodeDeployException(deployExceptionNode, deployException.getMessage(), deployException);
            }
        }
        log.info("Flow [{}] deployed", getTitle());
    }

    public void destroy() {
        for (Node node : Lists.reverse(pipeline)) {
            try {
                log.info("{} destroy", node.getLogTitle());
                node.getEmitter().clear();
                node.onDestroy();
            } catch (Exception e) {
                log.warn("{} destroy error: {}", node.getLogTitle(), e.getMessage(), e);
            }
        }
        getEmitter().clear();
        log.info("Flow [{}] destroyed", getTitle());
    }

    public <T extends Node> Optional<T> getNode(Class<T> nodeClass) {
        return pipeline.stream()
                .filter(nodeClass::isInstance)
                .map(nodeClass::cast)
                .findFirst();
    }

    public enum Event {

        /**
         * This event is thrown when there is an internal problem that cannot be automatically recovered.
         */
        BROKEN

    }

}
