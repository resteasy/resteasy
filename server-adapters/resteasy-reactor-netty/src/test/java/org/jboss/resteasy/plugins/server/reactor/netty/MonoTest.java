package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.time.Duration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;

public class MonoTest {

    @Path("/mono")
    public static class MonoResource {
        @GET
        public Mono<String> hello(@QueryParam("delay") Integer delayMs) {
            final Mono<String> businessLogic = Mono.just("Mono says hello!");

            return delayMs != null
                    ? businessLogic.delayElement(Duration.ofMillis(delayMs))
                    : businessLogic;
        }
    }

    private static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        ResteasyDeployment deployment = ReactorNettyContainer.start();
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(MonoResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test
    public void testNoDelayMono() {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            WebTarget target = client.target(generateURL("/mono"));
            String val = target.request().get(String.class);
            Assertions.assertEquals("Mono says hello!", val);
        });
    }

    @Test
    public void testDelayedMono() {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            WebTarget target = client.target(generateURL("/mono")).queryParam("delay", "1000");
            String val = target.request().get(String.class);
            Assertions.assertEquals("Mono says hello!", val);
        });
    }
}
