package com.javaflow.core.network;

import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import com.javaflow.util.LogUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

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
public class TcpRequest extends Node {

    private final String host;

    private final Integer port;

    private Integer connectTimeoutMs = 3000;

    @Setter(NONE)
    private NioSocketConnector connector;

    @Setter(NONE)
    private IoSession session;

    @Override
    public Msg invoke(Msg msg) {
        this.session.write(msg.payload());
        ReadFuture readFuture = this.session.read();
        readFuture.awaitUninterruptibly();
        Object message = readFuture.getMessage();
        msg.payload(message);
        return msg;
    }

    @Override
    public void onDeploy() {
        this.connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(connectTimeoutMs);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new AutoCodecFactory()));
        connector.getSessionConfig().setUseReadOperation(true);
        connector.getSessionConfig().setTcpNoDelay(true);
        connector.setHandler(new IoHandlerAdapter() {
            @Override
            public void sessionOpened(IoSession session) {
                log.info("{} session opened {}", getLogTitle(), getAddress());
            }

            @Override
            public void sessionClosed(IoSession session) {
                log.info("{} session closed {}", getLogTitle(), getAddress());
                getEmitter().emit(Event.SESSION_CLOSED, String.format("The session %s is closed", getAddress()));
            }

            @Override
            public void messageReceived(IoSession session, Object message) {
                log.info("{} message received {} {}", getLogTitle(), getAddress(), LogUtils.format(message));
            }
        });
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        this.session = future.getSession();
        log.info("{} session created {}", getLogTitle(), getAddress());
    }

    @Override
    public void onDestroy() {
        CloseFuture closeFuture = this.session.closeNow();
        closeFuture.awaitUninterruptibly(connectTimeoutMs);
        this.connector.dispose();
        log.info("{} connector dispose {}", getLogTitle(), getAddress());
    }

    private String getAddress() {
        return host + ":" + port;
    }

    private static class AutoCodecFactory implements ProtocolCodecFactory {

        private Class<?> messageClass;

        @Override
        public ProtocolEncoder getEncoder(IoSession session) {
            return new ProtocolEncoderAdapter() {
                @Override
                public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
                    messageClass = message.getClass();
                    if (message instanceof String) {
                        IoBuffer buffer = IoBuffer.wrap((message.toString()).getBytes(StandardCharsets.UTF_8));
                        out.write(buffer);
                    } else if (message instanceof byte[]) {
                        IoBuffer buffer = IoBuffer.wrap(((byte[]) message));
                        out.write(buffer);
                    } else {
                        out.write(message);
                    }
                }
            };
        }

        @Override
        public ProtocolDecoder getDecoder(IoSession session) {
            return new ProtocolDecoderAdapter() {
                @Override
                public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
                    if (String.class.isAssignableFrom(messageClass)) {
                        out.write(in.getString(StandardCharsets.UTF_8.newDecoder()));
                    } else if (byte[].class.isAssignableFrom(messageClass)) {
                        byte[] bytes = new byte[in.limit()];
                        in.get(bytes);
                        out.write(bytes);
                    } else {
                        out.write(in);
                    }
                }
            };
        }

    }

    public enum Event {

        SESSION_CLOSED

    }

}
