package com.developer.Simple.core;

import com.developer.Simple.HTTP.HTTPServer;
import com.developer.Simple.OkHttp;
import com.developer.Simple.Routers.Router;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RouterTest {
    static HTTPServer server;
    static OkHttp client;

    private static final int PORT = 8891;
    private static final String URL = "http://localhost:8891/%s";

    @BeforeClass
    public static void setup() throws Exception {
        Router router = new Router(routes -> {
            routes.put("echo", request ->
                    new ServerResponse(200, request.body.getBytes())
            );

            routes.put("invert", request -> {
                StringBuilder inverted = new StringBuilder();

                for (int i = request.body.length() - 1; i >= 0; i--) {
                    inverted.append(request.body.charAt(i));
                }

                return new ServerResponse(200, inverted.toString().getBytes());
            });

            routes.put("toLowerCase", request ->
                    new ServerResponse(200, request.body.toLowerCase().getBytes())
            );

            routes.put("Login", new Router(routes1 -> {
                routes1.put("index", clientRequest ->
                        (clientRequest.body.equals("Sup3rP#ssw0rd!@")) ?
                                new ServerResponse(200, "this is my super private index".getBytes()) :
                                new ServerResponse(401));

                routes1.put("secret", clientRequest ->
                        (clientRequest.body.equals("Sup3rP#ssw0rd!@")) ?
                                new ServerResponse(200, "this is a secret".getBytes()) :
                                new ServerResponse(401)
                );

                return routes1;
            }));

            return routes;
        });

        server = new HTTPServer(PORT, router);

        client = new OkHttp(false);
        try {
            server.setup();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void routeEcho() {
        try {
            Response response = client.syncRequest(String.format(URL, "echo"), "Hi");

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("Hi", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerInvert() {
        try {
            Response response = client.syncRequest(String.format(URL, "invert"), "Hi");

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("iH", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerToLowerCase() {
        try {
            Response response = client.syncRequest(String.format(URL, "toLowerCase"), "hi");

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("hi", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void routerLogin() {
        try {
            Response responseFail = client.syncRequest(String.format(URL, "Login"), "");

            Assert.assertEquals(401, responseFail.code());

            Response responseSuccess = client.syncRequest(String.format(URL, "Login"), "Sup3rP#ssw0rd!@");

            Assert.assertEquals(200, responseSuccess.code());
            Assert.assertNotNull(responseSuccess.body());
            Assert.assertEquals("this is my super private index", responseSuccess.body().string());

            Response responseSecret = client.syncRequest(String.format(URL, "Login/secret"), "Sup3rP#ssw0rd!@");

            Assert.assertEquals(200, responseSecret.code());
            Assert.assertNotNull(responseSecret.body());
            Assert.assertEquals("this is a secret", responseSecret.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}