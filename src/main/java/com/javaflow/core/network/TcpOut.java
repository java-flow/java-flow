package com.javaflow.core.network;

import com.javaflow.core.constant.MsgKeyConstant;
import com.javaflow.core.exception.NodeDeployException;
import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;

/**
 * @see TcpIn
 */
@Slf4j
public class TcpOut extends Node {

    @Override
    public Msg invoke(Msg msg) {
        IoSession ioSession = msg.get(TcpIn.MSG_SESSION, IoSession.class);
        if (msg.payload() == null) {
            log.warn("{} msg payload is null", getLogTitle());
        } else {
            ioSession.write(msg.payload());
        }
        if (Boolean.TRUE.equals(msg.get(MsgKeyConstant.FORBIDDEN))) {
            log.error("{} forbidden, disconnect", getLogTitle());
            ioSession.closeNow();
        }
        return msg;
    }

    @Override
    public void onDeploy() {
        if (getFlow().getNode(TcpIn.class).isEmpty()) {
            throw new NodeDeployException(this,
                    String.format("%s require a node [%s]", getLogTitle(), TcpIn.class.getSimpleName()));
        }
    }

}
