package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class BasicTest {
    private static Client client;
    private static String baseUrl;

    @BeforeClass
    public static void setup() throws Exception {
        final ResteasyDeployment deployment = ReactorNettyContainer.start();
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        final Client client1 = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        setupClient(client1);
        setupBaseUrl("http://%s:%d%s");
    }

    @AfterClass
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test(timeout = 1_000)
    public void get() {
        WebTarget target = client.target(generateURL("/basic"));
        String val = target.request().get(String.class);
        assertEquals("Hello world!", val);
    }

    @Test(timeout = 1_000)
    public void post() {
        sendBodyTest("POST");
    }

    @Test(timeout = 1_000)
    public void put() {
        sendBodyTest("PUT");
    }

    @Test(timeout = 1_000)
    public void delete() {
        sendBodyTest("DELETE");
    }

    @Test(timeout = 1_000)
    public void patch() {
        sendBodyTest("PATCH");
    }

    @Test(timeout = 1_000)
    public void head() {
        WebTarget target = client.target(generateURL("/basic"));
        try (Response resp = target.request().head()) {
            assertEquals(200, resp.getStatus());
            assertEquals("text/plain;charset=UTF-8", resp.getHeaderString("Content-Type"));
            assertNull(resp.getHeaderString("Content-Length"));
        }
    }

    @Test(timeout = 1_000)
    public void pojo() {
        final WebTarget target = client.target(generateURL("/basic/pojo"));
        final Response resp = target.request().get();
        assertEquals(42, resp.readEntity(BasicResource.Pojo.class).getAnswer());
    }

    public void sendBodyTest(final String method) {
        final String randomText = UUID.randomUUID().toString();
        final String resp = client.target(generateURL("/basic"))
                .request()
                .method(method, Entity.text(randomText), String.class);
        assertEquals(method.toUpperCase() + " " + randomText, resp);
    }

    public static void setupBaseUrl(final String baseUrlPath) {
        baseUrl = baseUrlPath;
    }

    public static void setupClient(Client jaxrsClient) {
        client = jaxrsClient;
    }

    // better to change this method over TestPortProvider utility class itself
    // this would do work for now
    public static String generateURL(final String path) {
        return String.format(baseUrl, getHost(), getPort(), path);
    }
}
