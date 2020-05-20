package com.developer.Simple;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.IOException;

// https://gist.github.com/MrNinso/29ff21cb39fd873bf3502605b5cb301f
@SuppressWarnings("RedundantThrows")
public class OkHttp {
    private final OkHttpClient client;

    public OkHttp(boolean ignoreCertificate) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (ignoreCertificate) {
            builder = configureToIgnoreCertificate(builder);
        }

        //Other application specific configuration

        client = builder.build();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Response syncRequest(String URL, @NotNull String body) throws IOException {
        Request request = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(body.getBytes()))
                .build();

        return client.newCall(request).execute();
    }

    //Setting testMode configuration. If set as testMode, the connection will skip certification check
    private OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }
}
