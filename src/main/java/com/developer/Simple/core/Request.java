package com.developer.Simple.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Request {
    public HttpContext httpContext;
    public HttpPrincipal httpPrincipal;
    public InetSocketAddress RemoreAddress;
    public Headers Headers;
    public String[] URI;
    public String protocol, Method, body;

    public static Request HttpExchangeToRequest(HttpExchange exchange) throws IOException {
        Request request = new Request();

        request.httpContext = exchange.getHttpContext();
        request.httpPrincipal = exchange.getPrincipal();
        request.RemoreAddress = exchange.getRemoteAddress();
        request.Headers = exchange.getRequestHeaders();
        request.protocol = exchange.getProtocol();
        request.Method = exchange.getRequestMethod();
        request.URI = exchange.getRequestURI().getPath().split("/");

        if (request.URI.length > 1) {
            request.URI = Arrays.copyOfRange(
                    request.URI,
                    1,
                    request.URI.length
            );
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(exchange.getRequestBody(), writer, StandardCharsets.UTF_8);
        request.body = writer.toString();

        return request;
    }
}
