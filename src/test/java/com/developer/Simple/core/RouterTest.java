package com.developer.Simple.core;

import com.developer.Simple.HTTP.HTTPServer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TreeMap;

public class RouterTest {
    static HTTPServer server;
    static HttpClient client;

    @BeforeClass
    public static void setup() throws Exception {
        TreeMap<String, Server.OnResquest> routes = new TreeMap<>();

        routes.put("echo", request ->
                new Response(200, request.body.getBytes())
        );

        routes.put("invert", request -> {
            StringBuilder inverted = new StringBuilder();

            for (int i = request.body.length()-1; i >= 0; i--) {
                inverted.append(request.body.charAt(i));
            }

            return new Response(200, inverted.toString().getBytes());
        });

        routes.put("toLowerCase", request ->
                new Response(200, request.body.toLowerCase().getBytes())
        );

        routes.put("Login", request -> {
            if (request.body.equals("Sup3rP#ssw0rd!@")) {
                TreeMap<String, Server.OnResquest> rs = new TreeMap<>();

                rs.put("index", request1 ->
                   new Response(200, "this is my super private index".getBytes())
                );

                rs.put("secret", request1 ->
                    new Response(200, "this is a secret".getBytes())
                );

                Router router = new Router(rs);

                return router.request(request);
            }
            return new Response(401);
        });

        server = new HTTPServer(8001, new Router(routes));

        client = HttpClient.newHttpClient();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void routeEcho() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/echo"))
                    .POST(HttpRequest.BodyPublishers.ofString("Hi"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, response.statusCode());
            Assert.assertEquals("Hi", response.body());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerInvert() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/invert"))
                    .POST(HttpRequest.BodyPublishers.ofString("Hi"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, response.statusCode());
            Assert.assertEquals("iH", response.body());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerToLowerCase() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/toLowerCase"))
                    .POST(HttpRequest.BodyPublishers.ofString("Hi"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, response.statusCode());
            Assert.assertEquals("hi", response.body());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerLogin() {
        try {
            HttpRequest requestFail = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/Login"))
                    .build();

            HttpResponse<String> responseFail = client.send(requestFail, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(401, responseFail.statusCode());

            HttpRequest requestSuccess = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/Login"))
                    .POST(HttpRequest.BodyPublishers.ofString("Sup3rP#ssw0rd!@"))
                    .build();

            HttpResponse<String> responseSuccess = client.send(requestSuccess, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, responseSuccess.statusCode());
            Assert.assertEquals("this is my super private index", responseSuccess.body());


            HttpRequest requestSuccessSecret = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8001/Login/secret"))
                    .POST(HttpRequest.BodyPublishers.ofString("Sup3rP#ssw0rd!@"))
                    .build();

            HttpResponse<String> responseSuccessSecret = client.send(requestSuccessSecret, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, responseSuccessSecret.statusCode());
            Assert.assertEquals("this is a secret", responseSuccessSecret.body());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}