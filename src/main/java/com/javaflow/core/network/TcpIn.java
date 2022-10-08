package com.javaflow.core.network;

import com.javaflow.core.exception.NodeDeployException;
import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static lombok.AccessLevel.NONE;

/**
 * @see <a href="https://mina.apache.org/mina-project/userguide/user-guide-toc.html">Mina</a>
 */
@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class TcpIn extends Node {

    public static final String MSG_SESSION = "session";

    private final Integer listenPort;

    private final ContentType contentType;

    @Setter(NONE)
    private NioSocketAcceptor acceptor;

    @Override
    public void onDeploy() {
        this.acceptor = new NioSocketAcceptor();
        // https://stackoverflow.com/questions/2411077/apache-mina-server-restart-java-net-bindexception-address-already-in-use
        acceptor.setReuseAddress(true);
        if (log.isTraceEnabled()) {
            acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        }
        if (ContentType.STRING.equals(contentType)) {
            acceptor.getFilterChain()
                    .addLast("codec", new ProtocolCodecFilter(new StringCodecFactory()));
        } else if (ContentType.BYTES.equals(contentType)) {
            acceptor.getFilterChain()
                    .addLast("codec", new ProtocolCodecFilter(new ByteArrayCodeFactory()));
        }
        acceptor.setHandler(new IoHandlerAdapter() {
            @Override
            public void sessionOpened(IoSession session) {
                log.info("{} connect count: {}", getLogTitle(), acceptor.getManagedSessionCount());
            }

            @Override
            public void sessionClosed(IoSession session) {
                log.info("{} connect count: {}", getLogTitle(), acceptor.getManagedSessionCount());
            }

            @Override
            public void messageReceived(IoSession session, Object message) {
                Msg msg = new Msg().payload(message);
                msg.put(MSG_SESSION, session);
                getFlow().nextInvoke(TcpIn.this, msg);
            }
        });
        try {
            acceptor.bind(new InetSocketAddress(listenPort));
            log.info("{} listen port: {}", getLogTitle(), listenPort);
        } catch (IOException e) {
            log.error("{} listen port: {} error: {}", getLogTitle(), listenPort, e.getMessage(), e);
            throw new NodeDeployException(this, e.getMessage(), e);
        }
    }

    @Override
    public void onDestroy() {
        if (acceptor == null) {
            return;
        }
        acceptor.dispose(true);
        log.info("{} cancel listen port: {}", getLogTitle(), listenPort);
    }

    public enum ContentType {

        STRING,

        BUFFER,

        BYTES

    }

    private static class StringCodecFactory implements ProtocolCodecFactory {

        @Override
        public ProtocolEncoder getEncoder(IoSession session) {
            return new ProtocolEncoderAdapter() {
                @Override
                public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
                    IoBuffer buffer = IoBuffer.wrap((message.toString()).getBytes(StandardCharsets.UTF_8));
                    out.write(buffer);
                }
            };
        }

        @Override
        public ProtocolDecoder getDecoder(IoSession session) {
            return new ProtocolDecoderAdapter() {
                @Override
                public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
                    out.write(in.getString(StandardCharsets.UTF_8.newDecoder()));
                }
            };
        }

    }

    private static class ByteArrayCodeFactory implements ProtocolCodecFactory {

        @Override
        public ProtocolEncoder getEncoder(IoSession session) {
            return new ProtocolEncoderAdapter() {
                @Override
                public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
                    IoBuffer buffer = IoBuffer.wrap(((byte[]) message));
                    out.write(buffer);
                }
            };
        }

        @Override
        public ProtocolDecoder getDecoder(IoSession session) {
            return new ProtocolDecoderAdapter() {
                @Override
                public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
                    byte[] bytes = new byte[in.limit()];
                    in.get(bytes);
                    out.write(bytes);
                }
            };
        }

    }

}
