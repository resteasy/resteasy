package org.jboss.resteasy.test.core.servlet;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.servlet.resource.FilterForwardServlet;
import org.jboss.resteasy.test.core.servlet.resource.UndertowServlet;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-903
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class UndertowTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
                .addClasses(UndertowServlet.class, FilterForwardServlet.class, UndertowTest.class, TestUtil.class,
                        PortProviderUtil.class)
                .addAsWebInfResource(ServletConfigTest.class.getPackage(), "UndertowWeb.xml", "web.xml");
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, "RESTEASY-903");
    }

    /**
     * @tpTestDetails Redirection in one servlet to other servlet.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUndertow() throws Exception {
        URL url = new URL(generateURL("/test"));
        HttpURLConnection conn = HttpURLConnection.class.cast(url.openConnection());
        conn.connect();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        conn.getInputStream().transferTo(out);
        Assertions.assertTrue(out.toString(StandardCharsets.UTF_8).startsWith("forward"),
                () -> "Wrong content of response: " + out);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, conn.getResponseCode());
        conn.disconnect();
    }
}
