package com.developer.Simple.HTTP;


import com.developer.Simple.core.HTTPCodes;
import com.developer.Simple.core.ServerResponse;
import com.sun.net.httpserver.HttpExchange;
import okhttp3.*;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HTTPServerTest {
    static HTTPServer server;
    static OkHttpClient client;

    private static final int PORT = 9188;
    private static final String URL = "http://localhost:9188";

    private static final ArrayList<Long> RequestTime = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        server = new HTTPServer(PORT, request ->
                new ServerResponse(HTTPCodes.OK, "Okay".getBytes())
        ) {
            @Override
            public void sendResponse(HttpExchange exchange, ServerResponse serverResponse) throws IOException {
                LocalDateTime now = LocalDateTime.now();

                super.sendResponse(exchange, serverResponse);

                RequestTime.add(now.until(LocalDateTime.now(), ChronoUnit.MICROS));
            }
        };

        client = new OkHttpClient();
        try {
            server.setup();
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
                .url(URL)
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

        int requestsCount = 14146;

        AtomicInteger count = new AtomicInteger(requestsCount);

        for (int i = 0; i < count.get(); i++) {
            requests.add(new Request.Builder()
                .url(URL)
                .build()
            );
        }

        LocalDateTime init = LocalDateTime.now();
        final LocalDateTime[] localTime = new LocalDateTime[1];
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
                        localTime[0] = LocalDateTime.now();
                    }
                }
            }
        ));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Total requests %d/%d", requestsCount - count.get(), requestsCount));
        Assert.assertEquals(0, count.get());
        System.out.println(String.format("Test time %d ms", init.until(localTime[0], ChronoUnit.MILLIS)));

        final long[] max = {0L};
        final long[] min = {RequestTime.get(0)};
        final long[] sum = {0};

        RequestTime.forEach(time -> {
            if (time > max[0]) {
                max[0] = time;
            }
            if (time < min[0]) {
                min[0] = time;
            }

            sum[0] += time;

        });

        long avg = sum[0]/RequestTime.size();

        System.out.println(String.format("min: %d µs, max: %d µs, avg: %d µs", min[0], max[0], avg));
    }
}