package com.developer.Simple.HTTPS;

import com.developer.Simple.OkHttp;
import com.developer.Simple.core.ServerResponse;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

public class HTTPSServerTest {

    static OkHttp client;

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

            HTTPSServer httpsServer = new HTTPSServer(PORT, sslContext, clientRequest ->
                    new ServerResponse(200, clientRequest.body.getBytes())
            );

            client = new OkHttp(true);

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
            Response response = client.syncRequest(URL, "Hi");

            Assert.assertEquals(200, response.code());
            Assert.assertNotNull(response.body());
            Assert.assertEquals("Hi", response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}