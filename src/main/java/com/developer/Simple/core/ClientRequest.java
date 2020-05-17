package com.developer.Simple.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ClientRequest {
    public HttpContext httpContext;
    public HttpPrincipal httpPrincipal;
    public InetSocketAddress RemoreAddress;
    public Headers Headers;
    public String[] URI;
    public String protocol, Method, body;

    public ClientRequest(HttpExchange exchange) throws IOException {
        this.httpContext = exchange.getHttpContext();
        this.httpPrincipal = exchange.getPrincipal();
        this.RemoreAddress = exchange.getRemoteAddress();
        this.Headers = exchange.getRequestHeaders();
        this.protocol = exchange.getProtocol();
        this.Method = exchange.getRequestMethod();
        this.URI = StringUtils.split(exchange.getRequestURI().getPath(), '/');

        this.body = new String(IOUtils.toByteArray(exchange.getRequestBody()));
    }
}
