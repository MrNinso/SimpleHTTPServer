package com.developer.Simple.Routers;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.Server;
import com.developer.Simple.core.ServerResponse;
import org.apache.commons.lang3.ArrayUtils;

import java.util.TreeMap;

public class Router implements Server.OnResquest {

    private final TreeMap<String, Server.OnResquest> Routes;

    public Router(TreeMap<String, Server.OnResquest> routes) {
        Routes = routes;
    }

    public Router(Builder b) {
        Routes = b.build(new TreeMap<>());
    }

    @Override
    public ServerResponse request(ClientRequest clientRequest) {
        String s;

        if (clientRequest.URI.length >= 1) {
            s = clientRequest.URI[0];
            clientRequest.URI = ArrayUtils.remove(clientRequest.URI, 0);
        } else {
            s = "index";
        }

        Server.OnResquest r = getRoutes().get(s);

        return r != null ? r.request(clientRequest) : new ServerResponse(404);
    }

    public TreeMap<String, Server.OnResquest> getRoutes() {
        return Routes;
    }

    public interface Builder {
        TreeMap<String, Server.OnResquest> build(TreeMap<String, Server.OnResquest> routes);
    }
}
