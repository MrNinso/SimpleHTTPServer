package com.developer.Simple.core;

import java.net.InetAddress;

public abstract class Server {

    private InetAddress Bind;
    private final int Port;
    private final OnResquest RequestHandler;

    public Server(int port, OnResquest requestHandler) throws Exception {
        this.Port = port;
        this.RequestHandler = requestHandler;

        setup();
    }

    public Server(InetAddress bind, int port, OnResquest requestHandler) throws Exception {
        this.Bind = bind;
        this.Port = port;
        this.RequestHandler = requestHandler;


        setup();
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

    protected abstract void setup() throws Exception;

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    public interface OnResquest {
        Response request(Request request);
    }
}
