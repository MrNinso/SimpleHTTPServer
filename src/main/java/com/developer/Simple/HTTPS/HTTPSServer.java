package com.developer.Simple.HTTPS;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.HTTPCodes;
import com.developer.Simple.core.Server;
import com.developer.Simple.core.ServerResponse;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class HTTPSServer extends Server {

    private HttpsServer httpsServer;
    private final SSLContext sslContext;

    public HTTPSServer(int port, SSLContext context, OnResquest requestHandler) {
        super(port, requestHandler);
        sslContext = context;
    }

    public HTTPSServer(InetAddress bind, int port, SSLContext context, OnResquest requestHandler) {
        super(bind, port, requestHandler);
        sslContext = context;
    }

    @Override
    public void setup() throws Exception {
        httpsServer = HttpsServer.create(new InetSocketAddress(getBind(), getPort()), 0);

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                SSLEngine engine = sslContext.createSSLEngine();

                params.setCipherSuites(engine.getEnabledCipherSuites());
                params.setNeedClientAuth(engine.getNeedClientAuth());
                params.setProtocols(engine.getEnabledProtocols());
                params.setSSLParameters(engine.getSSLParameters());
                params.setWantClientAuth(engine.getWantClientAuth());
            }
        });

        httpsServer.createContext("/", exchange -> {
            ServerResponse r = getRequestHandler().request(new ClientRequest(exchange));

            sendResponse(exchange, (r == null) ? new ServerResponse(HTTPCodes.INTERNAL_SERVER_ERROR) : r);
        });
    }

    @Override
    public void start() throws Exception {
        if (httpsServer == null) {
            setup();
        }
        httpsServer.start();
    }

    @Override
    public void stop() {
        if (httpsServer != null)
            httpsServer.stop(0);
    }

}
