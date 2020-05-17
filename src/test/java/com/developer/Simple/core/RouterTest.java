package com.developer.Simple.core;

import com.developer.Simple.HTTP.HTTPServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RouterTest {
    static HTTPServer server;
    static OkHttpClient client;

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

        server = new HTTPServer(8001, router);

        client = new OkHttpClient();
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
            Request request = new Request.Builder()
                    .url("http://localhost:8001/echo")
                    .post(RequestBody.create("Hi".getBytes()))
                    .build();


            Response response = client.newCall(request).execute();

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
            Request request = new Request.Builder()
                    .url("http://localhost:8001/invert")
                    .post(RequestBody.create("Hi".getBytes()))
                    .build();


            Response response = client.newCall(request).execute();

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
            Request request = new Request.Builder()
                    .url("http://localhost:8001/toLowerCase")
                    .post(RequestBody.create("Hi".getBytes()))
                    .build();


            Response response = client.newCall(request).execute();

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
            Request requestFail = new Request.Builder()
                    .url("http://localhost:8001/Login")
                    .build();


            Response responseFail = client.newCall(requestFail).execute();

            Assert.assertEquals(401, responseFail.code());

            Request requestSuccess = new Request.Builder()
                    .url("http://localhost:8001/Login")
                    .post(RequestBody.create("Sup3rP#ssw0rd!@".getBytes()))
                    .build();


            Response responseSuccess = client.newCall(requestSuccess).execute();

            Assert.assertEquals(200, responseSuccess.code());
            Assert.assertNotNull(responseSuccess.body());
            Assert.assertEquals("this is my super private index", responseSuccess.body().string());

            Request requestSecret = new Request.Builder()
                    .url("http://localhost:8001/Login/secret")
                    .post(RequestBody.create("Sup3rP#ssw0rd!@".getBytes()))
                    .build();

            Response responseSecret = client.newCall(requestSecret).execute();

            Assert.assertEquals(200, responseSecret.code());
            Assert.assertNotNull(responseSecret.body());
            Assert.assertEquals("this is a secret", responseSecret.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}