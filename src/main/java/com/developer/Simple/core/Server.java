package com.developer.Simple.core;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public abstract class Server {

    private InetAddress Bind;
    private final int Port;
    private final OnResquest RequestHandler;

    public Server(int port, OnResquest requestHandler) {
        this.Port = port;
        this.RequestHandler = requestHandler;
    }

    public Server(InetAddress bind, int port, OnResquest requestHandler) {
        this.Bind = bind;
        this.Port = port;
        this.RequestHandler = requestHandler;
    }

    public InetAddress getBind() {
        return Bind;
    }

    public int getPort() {
        return Port;
    }

    public OnResquest getRequestHandler() {
        return RequestHandler;
    }

    protected void sendResponse(HttpExchange exchange, ServerResponse serverResponse) throws IOException {
        serverResponse.responsHeader.forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value)
        );

        exchange.sendResponseHeaders(serverResponse.HttpCode, serverResponse.responseBody.length);
        OutputStream os = exchange.getResponseBody();
        os.write(serverResponse.responseBody);

        os.close();
    }

    public abstract void setup() throws Exception;

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    public interface OnResquest {
        ServerResponse request(ClientRequest clientRequest);
    }
}
