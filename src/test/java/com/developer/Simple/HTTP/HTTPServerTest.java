package com.developer.Simple.HTTP;


import com.developer.Simple.core.ServerResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HTTPServerTest {
    static HTTPServer server;
    static OkHttpClient client;

    @BeforeClass
    public static void setup() throws Exception {
        server = new HTTPServer(8000, request ->
                new ServerResponse(200, "Okay".getBytes())
        );

        client = new OkHttpClient();
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
            Request request = new Request.Builder()
                .url("http://localhost:8000")
                .build();


            Response response = client.newCall(request).execute();

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("Okay", response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void stressTest() {
        List<Request> requests = new ArrayList<>();

        AtomicInteger count = new AtomicInteger(14146);

        for (int i = 0; i < count.get(); i++) {
            requests.add(new Request.Builder()
                .url("http://localhost:8000")
                .build()
            );
        }

        Date init = new Date();
        System.out.println(init);
        requests.forEach(httpRequest ->
            client.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Assert.fail(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Assert.assertNotNull(response.body());
                    Assert.assertEquals("Okay", response.body().string());
                    int i = count.decrementAndGet();

                    if (i == 0) {
                        Date end = new Date();
                        System.out.println(end);
                    }
                }
            }
        ));

        try {
            Thread.sleep(141460);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(0, count.get());
    }
}