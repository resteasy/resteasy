package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.time.Duration;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest {
    static String BASE_URI = generateURL("");
    static Client client;

    static final int REQUEST_TIMEOUT = 4000;

    @BeforeAll
    public static void setupSuite() throws Exception {
        NettyContainer.start().getRegistry().addSingletonResource(new AsyncJaxrsResource());
    }

    @AfterAll
    public static void tearDownSuite() throws Exception {
        NettyContainer.stop();
    }

    @BeforeEach
    public void setupTest() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void tearDownTest() throws Exception {
        client.close();
    }

    @Test
    public void testInjectionFailure() {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            //      System.out.println("***INJECTION FAILURE***");
            Response response = client.target(BASE_URI).path("jaxrs/injection-failure/abcd").request().get();
            Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            response.close();
        });
    }

    @Test
    public void testMethodFailure() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            //      System.out.println("***method FAILURE***");
            Response response = client.target(BASE_URI).path("jaxrs/method-failure").request().get();
            Assertions.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
            response.close();
        });
    }

    @Test
    public void testAsync() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            callAsync(client);
            //callAsync(client);
            //callAsync(client);
        });
    }

    private void callAsync(Client client) {
        Response response = client.target(BASE_URI).path("jaxrs").request().get();
        Assertions.assertEquals(200, response.getStatus());
        //      System.out.println(response.getHeaders().size());
        //      System.out.println(response.getHeaders().keySet().iterator().next());
        Assertions.assertEquals("hello", response.readEntity(String.class));
        response.close();
    }

    @Test
    public void testEmpty() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3 * REQUEST_TIMEOUT), () -> {
            callEmpty(client);
        });
    }

    private void callEmpty(Client client) {
        long start = System.currentTimeMillis();
        Response response = client.target(BASE_URI).path("jaxrs/empty").request().get();
        long end = System.currentTimeMillis() - start;
        Assertions.assertEquals(204, response.getStatus());
        Assertions.assertTrue(end < REQUEST_TIMEOUT); // should take less than 1 second
        response.close();
    }

    @Test
    public void testTimeout() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            Response response = client.target(BASE_URI).path("jaxrs/timeout").request().get();
            Assertions.assertEquals(503, response.getStatus());
            response.close();
        });
    }

    @Test
    public void testCancelled() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            Response response = null;
            response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
            Assertions.assertEquals(204, response.getStatus());
            response.close();

            Thread.sleep(100);

            response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
            Assertions.assertEquals(500, response.getStatus());
            response.close();

            Thread.sleep(100);
        });
    }

    @Test
    public void testCancel() throws Exception {
        Response response = null;
        response = client.target(BASE_URI).path("jaxrs/cancelled").request().put(null);
        Assertions.assertEquals(204, response.getStatus());
        response.close();

        Thread.sleep(100);

        response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
        Assertions.assertEquals(500, response.getStatus());
        response.close();

        Thread.sleep(100);

        response = client.target(BASE_URI).path("jaxrs/cancel").request().get();
        Assertions.assertEquals(503, response.getStatus());
        response.close();

        Thread.sleep(100);

        response = client.target(BASE_URI).path("jaxrs/cancelled").request().get();
        Assertions.assertEquals(204, response.getStatus());
        response.close();

        Thread.sleep(100);
    }

    @Test
    public void testResumeObject() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            Response response = client.target(BASE_URI).path("jaxrs/resume/object").request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("bill", response.readEntity(XmlData.class).getName());
            response.close();
        });
    }

    @Test
    public void testResumeObjectThread() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            Response response = client.target(BASE_URI).path("jaxrs/resume/object/thread").request().get();
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("bill", response.readEntity(XmlData.class).getName());
            response.close();
        });
    }

    @Test
    public void testConnectionCloseHeader() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(REQUEST_TIMEOUT), () -> {
            Builder requestBuilder = client.target(BASE_URI).path("jaxrs/empty").request();
            requestBuilder.header("Connection", "close");
            Response response = requestBuilder.get();
            Assertions.assertEquals(HttpHeaderValues.CLOSE.toString(), response.getHeaderString("Connection"));
            response.close();
        });
    }
}