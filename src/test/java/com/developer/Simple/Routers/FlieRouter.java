package com.developer.Simple.Routers;

import com.developer.Simple.HTTP.HTTPServer;
import com.developer.Simple.HTTPS.HTTPSServer;
import com.developer.Simple.OkHttp;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
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

public class FlieRouter {
    private static OkHttp client;

    private static final int PORT_HTTP = 2200;
    private static final int PORT_HTTPS = 2201;

    private static final String HTTP_URL = "http://localhost:2200/%s";
    private static final String HTTPS_URL = "https://localhost:2201/%s";

    private static final Path RESOURCE_DIRECTORY = Paths.get("src","test","resources");
    private static final File SITE_ROOT = new File(RESOURCE_DIRECTORY.toFile(), "SampleSite");
    private static final File HOME_HTML = new File(SITE_ROOT, "home.html");
    private static final File RANDOM = new File(SITE_ROOT, "r.random");

    @BeforeClass
    public static void setup() {
        try {
            Assert.assertTrue(SITE_ROOT.exists());
            Assert.assertTrue(HOME_HTML.exists());
            Assert.assertTrue(RANDOM.exists());

            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");

            FileRouter router = new FileRouter(SITE_ROOT, "home.html");


            File cert = new File(RESOURCE_DIRECTORY.toFile(), "lig.keystore");

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

            HTTPSServer httpsServer = new HTTPSServer(PORT_HTTPS, sslContext, router);

            client = new OkHttp(true);

            httpsServer.setup();
            httpsServer.start();

            HTTPServer httpServer = new HTTPServer(PORT_HTTP, router);

            httpServer.setup();
            httpServer.start();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHttpRouter() {
        try {
            Response responseIndex = client. syncRequest(String.format(HTTP_URL, ""), "");

            Assert.assertEquals(200, responseIndex.code());
            Assert.assertNotNull(responseIndex.body());
            Assert.assertEquals(FileUtils.readFileToString(HOME_HTML, "UTF-8"), responseIndex.body().string());

            Assert.assertEquals(404, client.syncRequest(String.format(HTTP_URL, "ImThinking"), "").code());

            Response responseFile = client.syncRequest(String.format(HTTP_URL, RANDOM.getName()), "");

            Assert.assertEquals(200, responseFile.code());
            Assert.assertNotNull(responseFile.body());
            Assert.assertEquals(FileUtils.readFileToString(RANDOM, "UTF-8"), responseFile.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHttpsRouter() {
        try {
            Response responseIndex = client.syncRequest(String.format(HTTPS_URL, ""), "");

            Assert.assertEquals(200, responseIndex.code());
            Assert.assertNotNull(responseIndex.body());
            Assert.assertEquals(FileUtils.readFileToString(HOME_HTML, "UTF-8"), responseIndex.body().string());

            Assert.assertEquals(404, client.syncRequest(String.format(HTTPS_URL, "ImThinking"), "").code());

            Response responseFile = client.syncRequest(String.format(HTTPS_URL, RANDOM.getName()), "");

            Assert.assertEquals(200, responseFile.code());
            Assert.assertNotNull(responseFile.body());
            Assert.assertEquals(FileUtils.readFileToString(RANDOM, "UTF-8"), responseFile.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
