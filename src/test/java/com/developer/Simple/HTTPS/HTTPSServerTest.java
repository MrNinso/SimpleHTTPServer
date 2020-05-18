package com.developer.Simple.HTTPS;

import com.developer.Simple.core.ServerResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

public class HTTPSServerTest {

    private static HTTPSServer httpsServer;
    static OkHttpClient client;

    private static final int PORT = 8855;
    private static final String URL = "https://localhost:8855";

    @BeforeClass
    public static void setup() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");

            Path resourceDirectory = Paths.get("src","test","resources");
            File cert = new File(resourceDirectory.toFile(), "lig.keystore");

            if (!cert.exists() || !cert.canRead()) {
                throw new Exception("Erro when loading cert file");
            }

            InputStream is = new FileInputStream(cert);

            ks.load(is, password);

            // Set up the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // Set up the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // Set up the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            httpsServer = new HTTPSServer(PORT, sslContext, clientRequest ->
                    new ServerResponse(200, clientRequest.body.getBytes())
            );

            int index = -1;

            for (int i = 0; i < tmf.getTrustManagers().length; i++) {
                if (tmf.getTrustManagers()[i] instanceof X509TrustManager) {
                    index = i;
                    break;
                }
            }

            client = client = new OkHttpClient();

            httpsServer.setup();
            httpsServer.start();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void simpleRequest() {
        try {
            Request request = new Request.Builder()
                    .url(URL)
                    .post(RequestBody.create("Hi".getBytes()))
                    .build();


            Response response = client.newCall(request).execute();

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("Hi", response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}