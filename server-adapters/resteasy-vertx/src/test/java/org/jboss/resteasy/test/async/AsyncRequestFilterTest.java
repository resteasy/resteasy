package org.jboss.resteasy.test.async;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AsyncRequestFilterTest {
    protected static final Logger log = Logger.getLogger(AsyncRequestFilterTest.class.getName());

    static void register(VertxResteasyDeployment deployment, Class... list) {
        for (Class clazz : list) {
            deployment.getProviderFactory().registerProvider(clazz);
        }

    }

    @BeforeAll
    public static void setup() throws Exception {
        VertxResteasyDeployment deployment = VertxContainer.start();
        deployment.getRegistry().addPerRequestResource(AsyncRequestFilterResource.class);
        register(deployment,
                AsyncRequestFilter1.class, AsyncRequestFilter2.class, AsyncRequestFilter3.class,
                AsyncPreMatchRequestFilter1.class, AsyncPreMatchRequestFilter2.class, AsyncPreMatchRequestFilter3.class,
                AsyncResponseFilter1.class, AsyncResponseFilter2.class, AsyncResponseFilter3.class,
                AsyncFilterException.class, AsyncFilterExceptionMapper.class);

    }

    @AfterAll
    public static void end() throws Exception {
        VertxContainer.stop();
    }

    /**
     * @tpTestDetails Interceptors work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testRequestFilters() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/"));

        // all sync

        Response response = base.request()
                .header("Filter1", "sync-pass")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "sync-fail")
                .header("Filter2", "sync-fail")
                .header("Filter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter1", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "sync-pass")
                .header("Filter2", "sync-fail")
                .header("Filter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter2", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "sync-pass")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter3", response.readEntity(String.class));

        // async
        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "async-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "async-pass")
                .header("Filter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "sync-pass")
                .header("Filter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "sync-pass")
                .header("Filter2", "async-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        // async failures

        response = base.request()
                .header("Filter1", "async-fail")
                .header("Filter2", "sync-fail")
                .header("Filter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter1", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "sync-fail")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter2", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "async-fail")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter2", response.readEntity(String.class));

        // async instantaneous
        response = base.request()
                .header("Filter1", "async-pass-instant")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("Filter1", "async-fail-instant")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Filter1", response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Interceptors work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testPreMatchRequestFilters() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/"));

        // all sync

        Response response = base.request()
                .header("PreMatchFilter1", "sync-pass")
                .header("PreMatchFilter2", "sync-pass")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "sync-fail")
                .header("PreMatchFilter2", "sync-fail")
                .header("PreMatchFilter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter1", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "sync-pass")
                .header("PreMatchFilter2", "sync-fail")
                .header("PreMatchFilter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter2", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "sync-pass")
                .header("PreMatchFilter2", "sync-pass")
                .header("PreMatchFilter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter3", response.readEntity(String.class));

        // async
        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "sync-pass")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals("resource", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());

        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "async-pass")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "async-pass")
                .header("PreMatchFilter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "sync-pass")
                .header("PreMatchFilter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "sync-pass")
                .header("PreMatchFilter2", "async-pass")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        // async failures

        response = base.request()
                .header("PreMatchFilter1", "async-fail")
                .header("PreMatchFilter2", "sync-fail")
                .header("PreMatchFilter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter1", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "sync-fail")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter2", response.readEntity(String.class));

        response = base.request()
                .header("PreMatchFilter1", "async-pass")
                .header("PreMatchFilter2", "async-fail")
                .header("PreMatchFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("PreMatchFilter2", response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Interceptors work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testResponseFilters() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/"));

        // all sync

        Response response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "sync-fail")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter1", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "sync-fail")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter2", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-fail")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter3", response.readEntity(String.class));

        // async
        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals("resource", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());

        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "async-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "async-pass")
                .header("ResponseFilter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "async-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "async-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        // async failures

        response = base.request()
                .header("ResponseFilter1", "async-fail")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter1", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "sync-fail")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter2", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "async-pass")
                .header("ResponseFilter2", "async-fail")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter2", response.readEntity(String.class));

        // async instantaneous
        response = base.request()
                .header("ResponseFilter1", "async-pass-instant")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));

        response = base.request()
                .header("ResponseFilter1", "async-fail-instant")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter1", response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Interceptors work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testResponseFilters2() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/async"));

        // async way later
        Response response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("ResponseFilter3", "async-fail-late")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ResponseFilter3", response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async filters work with resume(Throwable) wrt filters/callbacks/complete
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testResponseFiltersThrow() throws Exception {
        Client client = ClientBuilder.newClient();

        testResponseFilterThrow(client, "/callback-async", false);
        testResponseFilterThrow(client, "/callback", false);

        testResponseFilterThrow(client, "/callback-async", true);
        testResponseFilterThrow(client, "/callback", true);

        client.close();
    }

    private void testResponseFilterThrow(Client client, String target, boolean useExceptionMapper) {
        WebTarget base = client.target(generateURL(target));

        // throw in response filter
        Response response = base.request()
                .header("ResponseFilter1", "sync-pass")
                .header("ResponseFilter2", "sync-pass")
                .header("UseExceptionMapper", useExceptionMapper)
                .header("ResponseFilter3", "async-throw-late")
                .get();
        // this is 500 even with exception mapper because exceptions in response filters are not mapped
        Assertions.assertEquals(500, response.getStatus());

        try {
            // give a chance to CI to run the callbacks
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // check that callbacks were called
        response = base.request().get();
        Assertions.assertEquals(200, response.getStatus());
        if (useExceptionMapper)
            Assertions.assertEquals("org.jboss.resteasy.test.async.AsyncFilterException: ouch",
                    response.getHeaders().getFirst("ResponseFilterCallbackResponseFilter3"));
        else
            Assertions.assertEquals("java.lang.Throwable: ouch",
                    response.getHeaders().getFirst("ResponseFilterCallbackResponseFilter3"));

        // throw in request filter
        response = base.request()
                .header("Filter1", "sync-pass")
                .header("Filter2", "sync-pass")
                .header("UseExceptionMapper", useExceptionMapper)
                .header("Filter3", "async-throw-late")
                .get();
        if (useExceptionMapper) {
            Assertions.assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());
            Assertions.assertEquals("exception was mapped", response.readEntity(String.class));
        } else {
            Assertions.assertEquals(500, response.getStatus());
        }

        try {
            // give a chance to CI to run the callbacks
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // check that callbacks were called
        response = base.request().get();
        Assertions.assertEquals(200, response.getStatus());
        if (useExceptionMapper)
            Assertions.assertEquals("org.jboss.resteasy.test.async.AsyncFilterException: ouch",
                    response.getHeaders().getFirst("RequestFilterCallbackFilter3"));
        else
            Assertions.assertEquals("java.lang.Throwable: ouch",
                    response.getHeaders().getFirst("RequestFilterCallbackFilter3"));

    }

    /**
     * @tpTestDetails Interceptors work with non-Response resource methods
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testRequestFiltersGuessReturnType() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/non-response"));

        Response response = base.request()
                .header("Filter1", "async-pass")
                .header("Filter2", "sync-pass")
                .header("Filter3", "sync-pass")
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("resource", response.readEntity(String.class));
        client.close();
    }
}
