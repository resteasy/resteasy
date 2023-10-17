package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;

import java.time.Duration;
import java.util.UUID;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class BasicTest {
    private static Client client;
    private static String baseUrl;

    @BeforeAll
    public static void setup() throws Exception {
        final ResteasyDeployment deployment = ReactorNettyContainer.start();
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        final Client client1 = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        setupClient(client1);
        setupBaseUrl("http://%s:%d%s");
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test
    public void get() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> {
            WebTarget target = client.target(generateURL("/basic"));
            String val = target.request().get(String.class);
            Assertions.assertEquals(val, "Hello world!");
        });
    }

    @Test
    public void post() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> sendBodyTest("POST"));
    }

    @Test
    public void put() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> sendBodyTest("PUT"));
    }

    @Test
    public void delete() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> sendBodyTest("DELETE"));
    }

    @Test
    public void patch() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> sendBodyTest("PATCH"));
    }

    @Test
    public void head() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> {
            WebTarget target = client.target(generateURL("/basic"));
            try (Response resp = target.request().head()) {
                Assertions.assertEquals(200, resp.getStatus());
                Assertions.assertEquals("text/plain;charset=UTF-8", resp.getHeaderString("Content-Type"));
                Assertions.assertNull(resp.getHeaderString("Content-Length"));
            }
        });
    }

    @Test
    public void pojo() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> {
            final WebTarget target = client.target(generateURL("/basic/pojo"));
            final Response resp = target.request().get();
            Assertions.assertEquals(42, resp.readEntity(BasicResource.Pojo.class).getAnswer());
        });
    }

    public void sendBodyTest(final String method) {
        final String randomText = UUID.randomUUID().toString();
        final String resp = client.target(generateURL("/basic"))
                .request()
                .method(method, Entity.text(randomText), String.class);
        Assertions.assertEquals(method.toUpperCase() + " " + randomText, resp);
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
