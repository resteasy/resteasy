package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpContextTest {

    private static HttpServer httpServer;
    private static HttpContextBuilder contextBuilder;

    @BeforeAll
    public static void before() throws Exception {
        int port = TestPortProvider.getPort();
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(SimpleResource.class);
        contextBuilder.bind(httpServer);
        httpServer.start();

    }

    @AfterAll
    public static void after() throws Exception {
        contextBuilder.cleanup();
        httpServer.stop(1);
    }

    @Test
    public void testNoDefaultsResource() throws Exception {
        Client client = ClientBuilder.newClient();

        {
            Response response = client.target(generateURL("/basic")).request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("basic", response.readEntity(String.class));
        }

        {
            Response response = client.target(generateURL("/basic")).request().put(Entity.entity("basic", "text/plain"));
            Assertions.assertEquals(204, response.getStatus());
            response.close();
        }

        {
            Response response = client.target(generateURL("/queryParam")).queryParam("param", "hello world").request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("hello world", response.readEntity(String.class));
        }

        {
            Response response = client.target(generateURL("/uriParam/1234")).request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("1234", response.readEntity(String.class));
        }

        {
            Response response = client.target(generateURL("/request")).request().get();
            Assertions.assertEquals(200, response.getStatus());
            final String val = response.readEntity(String.class);
            final String pattern = "^127.0.0.1/.+";
            Assertions.assertTrue(Pattern.matches(pattern, val),
                    String.format("Expected value '%s' to match pattern '%s'", val, pattern));
        }
    }

}
