package com.javaflow.core.network;

import com.javaflow.core.constant.MsgKeyConstant;
import com.javaflow.core.exception.NodeDeployException;
import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import spark.Response;

import static com.javaflow.core.network.HttpIn.MSG_CONTENT_TYPE;
import static com.javaflow.core.network.HttpIn.MSG_RESPONSE;

/**
 * @see HttpIn
 */
@Slf4j
public class HttpOut extends Node {

    @Override
    public Msg invoke(Msg msg) {
        Response response = msg.get(MSG_RESPONSE, Response.class);
        String payload = msg.payloadAs(String.class);
        response.type(msg.getOrDefault(MSG_CONTENT_TYPE, String.class, response.type()));
        response.body(payload);
        if (Boolean.TRUE.equals(msg.get(MsgKeyConstant.FORBIDDEN))) {
            log.error("{} forbidden, response status: {}", getFlow(), HttpStatus.FORBIDDEN_403);
            response.status(HttpStatus.FORBIDDEN_403);
        }
        return msg;
    }

    @Override
    public void onDeploy() {
        if (getFlow().getNode(HttpIn.class).isEmpty()) {
            throw new NodeDeployException(this,
                    String.format("%s require a node [%s]", getLogTitle(), HttpIn.class.getSimpleName()));
        }
    }

}
