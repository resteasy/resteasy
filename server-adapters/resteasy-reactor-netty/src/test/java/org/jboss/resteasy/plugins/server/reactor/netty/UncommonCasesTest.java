package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UncommonCasesTest {

    private static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        final ResteasyDeployment deployment = ReactorNettyContainer.start();
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    /**
     * Verifies that no errors occurs in the event one or multiple requests are sent with empty body.
     */
    @Test
    public void testEmptyBody() {

        final WebTarget target = client.target(generateURL("/basic"));
        final Response resp = target.request().post(null);
        Assertions.assertEquals(200, resp.getStatus());
        Assertions.assertEquals("POST ", resp.readEntity(String.class));

        final Response resp2 = target.request().put(null);
        Assertions.assertEquals(200, resp2.getStatus());
        Assertions.assertEquals("PUT ", resp2.readEntity(String.class));

    }

}
