package com.developer.Simple.HTTP;

import com.developer.Simple.core.Request;
import com.developer.Simple.core.Response;
import com.developer.Simple.core.Server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

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
            Response r = getRequestHandler().request(Request.HttpExchangeToRequest(exchange));

            sendResponse(exchange, Objects.requireNonNullElseGet(r, () -> new Response(500)));
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

    private void sendResponse(HttpExchange exchange, Response response) throws IOException {
        response.responsHeader.forEach((key, value) ->
                exchange.getResponseHeaders().add(key, value)
        );

        exchange.sendResponseHeaders(response.HttpCode, response.responseBody.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.responseBody);

        os.close();
    }
}
