package com.developer.Simple.HTTP;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.HTTPCodes;
import com.developer.Simple.core.ServerResponse;
import com.developer.Simple.core.Server;
import com.sun.net.httpserver.HttpServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@SuppressWarnings("RedundantThrows")
public class HTTPServer extends Server {

    private HttpServer httpServer;

    public HTTPServer(int port, OnResquest requestHandler) {
        super(port, requestHandler);
    }

    public HTTPServer(InetAddress bind, int port, OnResquest requestHandler) {
        super(bind, port, requestHandler);
    }

    @Override
    public void setup() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(getBind(), getPort()), 0);
        httpServer.createContext("/", exchange -> {
            ServerResponse r = getRequestHandler().request(new ClientRequest(exchange));

            sendResponse(exchange, (r == null) ? new ServerResponse(HTTPCodes.INTERNAL_SERVER_ERROR) : r);
        });

    }

    @Override
    public void start() throws Exception {
        if (httpServer == null) {
            setup();
        }
        httpServer.start();
    }

    @Override
    public void stop() throws Exception {
        httpServer.stop(0);
    }
}
