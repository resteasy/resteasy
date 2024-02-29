package org.jboss.resteasy.test.core.servlet;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.ReflectPermission;
import java.net.HttpURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.PropertyPermission;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.servlet.resource.FilterForwardServlet;
import org.jboss.resteasy.test.core.servlet.resource.UndertowServlet;
import org.jboss.resteasy.utils.PermissionUtil;
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
        WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
                .addClasses(UndertowServlet.class, FilterForwardServlet.class, UndertowTest.class, TestUtil.class,
                        PortProviderUtil.class)
                .addAsWebInfResource(ServletConfigTest.class.getPackage(), "UndertowWeb.xml", "web.xml");
        // Arquillian in the deployment and use of PortProviderUtil
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new SocketPermission("[" + PortProviderUtil.getHost() + "]", "connect,resolve")), "permissions.xml");
        return war;
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
