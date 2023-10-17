package org.jboss.resteasy.test.async;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AsyncTest {

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        VertxContainer.start().getRegistry().addPerRequestResource(AsyncResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        VertxContainer.stop();
    }

    /**
     * @tpTestDetails Test for correct response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsync() throws Exception {
        Response response = client.target(generateURL("/async")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong response content");
    }

    /**
     * @tpTestDetails Service unavailable test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        Response response = client.target(generateURL("/async/timeout")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());
    }
}
