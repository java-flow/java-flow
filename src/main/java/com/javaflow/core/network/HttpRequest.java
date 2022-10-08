package com.javaflow.core.network;

import com.javaflow.core.support.Msg;
import com.javaflow.core.support.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class HttpRequest extends Node {

    /**
     * GET,POST,PUT,PATCH,DELETE...
     */
    private String method;

    private String url;

    @SneakyThrows
    @Override
    public Msg invoke(Msg msg) {
        String requestMethod = msg.getOrDefault(Fields.method, String.class, this.method);
        String requestUrl = msg.getOrDefault(Fields.url, String.class, this.url);
        HttpClient httpClient = HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .method(requestMethod, getBodyPublisher(requestUrl, msg))
                .uri(URI.create(requestUrl))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return msg.payload(response.body());
    }

    private BodyPublisher getBodyPublisher(String method, Msg msg) {
        if (msg.payload() == null || "GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return null;
        }
        return BodyPublishers.ofString(msg.payloadAsString());
    }

}
