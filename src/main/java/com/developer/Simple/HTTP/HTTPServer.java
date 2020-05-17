package com.developer.Simple.HTTP;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.ServerResponse;
import com.developer.Simple.core.Server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class HTTPServer extends Server {

    private HttpServer httpServer;

    public HTTPServer(int port, OnResquest requestHandler) throws Exception {
        super(port, requestHandler);
    }

    public HTTPServer(InetAddress bind, int port, OnResquest requestHandler) throws Exception {
        super(bind, port, requestHandler);
    }

    @Override
    protected void setup() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(getBind(), getPort()), 0);
        httpServer.createContext("/", exchange -> {
            ServerResponse r = getRequestHandler().request(new ClientRequest(exchange));

            sendResponse(exchange, (r == null) ? new ServerResponse(500) : r);
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

    private void sendResponse(HttpExchange exchange, ServerResponse serverResponse) throws IOException {
        serverResponse.responsHeader.forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value)
        );

        exchange.sendResponseHeaders(serverResponse.HttpCode, serverResponse.responseBody.length);
        OutputStream os = exchange.getResponseBody();
        os.write(serverResponse.responseBody);

        os.close();
    }
}
