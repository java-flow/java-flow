package com.javaflow.core.network;

import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import spark.RouteImpl;
import spark.Service;
import spark.route.HttpMethod;

import static lombok.AccessLevel.NONE;

/**
 * @see <a href="http://sparkjava.com/documentation.html">Spark</a>
 */
@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class HttpIn extends Node {

    public static final String MSG_REQUEST = "request";

    public static final String MSG_RESPONSE = "response";

    public static final String MSG_CONTENT_TYPE = "content-type";

    public static final String MSG_REQUEST_METHOD = "request-method";

    private final Integer listenPort;

    private final HttpMethod method;

    private final String url;

    @Setter(NONE)
    private Service service;

    public HttpIn(Integer listenPort, HttpMethod method, String url) {
        this.listenPort = listenPort;
        this.method = method;
        this.url = url;
    }

    public HttpIn(Service service, HttpMethod method, String url) {
        this.service = service;
        this.method = method;
        this.url = url;
        this.listenPort = service.port();
    }

    @Override
    public void onDeploy() {
        if (this.service == null) {
            this.service = Service.ignite();
            service.port(listenPort);
        }
        service.addRoute(method, RouteImpl.create(url, (request, response) -> {
            Msg msg = new Msg().payload(request.body());
            msg.put(MSG_REQUEST, request);
            msg.put(MSG_REQUEST_METHOD, request.requestMethod());
            msg.put(MSG_RESPONSE, response);
            msg.put(MSG_CONTENT_TYPE, request.contentType());
            getFlow().nextInvoke(HttpIn.this, msg);
            return response.body();
        }));
        log.info("{} listen port: {} method: {} url: {}", getLogTitle(), listenPort, method, url);
    }

    @Override
    public void onDestroy() {
        if (service == null) {
            return;
        }
        service.stop();
        service.awaitStop();
    }

    /**
     * For reuse listen port
     */
    public static Service createService(Integer listenPort) {
        Service service = Service.ignite().port(listenPort);
        service.initExceptionHandler(e -> log.error(e.getMessage(), e));
        service.init();
        return service;
    }

}
