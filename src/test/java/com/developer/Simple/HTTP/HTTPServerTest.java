package com.developer.Simple.HTTP;

import com.developer.Simple.core.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HTTPServerTest {
    static HTTPServer server;
    static HttpClient client;

    @BeforeClass
    public static void setup() throws Exception {
        server = new HTTPServer(8000, request ->
                new Response(200, "Okay".getBytes())
        );

        client = HttpClient.newHttpClient();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void connTest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000"))
                .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assert.assertEquals(200, response.statusCode());
            Assert.assertEquals("Okay", response.body());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void stressTest() {
        List<HttpRequest> requests = new ArrayList<>();

        AtomicInteger count = new AtomicInteger(1000);

        for (int i = 0; i < count.get(); i++) {
            requests.add(HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8000"))
                    .build()
            );
        }

        Date init = new Date();
        System.out.println(init);
        requests.forEach(httpRequest ->
            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> {
                   Assert.assertEquals("Okay", s);
                   int i = count.decrementAndGet();

                   if (i == 0) {
                       Date end = new Date();
                       System.out.println(end);
                   }
                   return s;
                }
            )
        );

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(0, count.get());
    }
}