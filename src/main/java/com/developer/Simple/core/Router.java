package com.developer.Simple.core;

import java.util.Arrays;
import java.util.TreeMap;

public class Router implements Server.OnResquest {

    private final TreeMap<String, Server.OnResquest> Routes;

    public Router(TreeMap<String, Server.OnResquest> routes) {
        Routes = routes;
    }

    @Override
    public Response request(Request request) {
        String s;

        if (request.URI.length >= 1) {
            s = request.URI[0];
            request.URI = Arrays.copyOfRange(
                    request.URI,
                    1,
                    request.URI.length
            );
        } else {
            s = "index";
        }

        Server.OnResquest r = getRoutes().get(s);

        return r != null ? r.request(request) : new Response(404);
    }

    public TreeMap<String, Server.OnResquest> getRoutes() {
        return Routes;
    }
}
