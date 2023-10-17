package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

public class ClientBadRequestTest {
    private static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        ResteasyDeployment deployment = ReactorNettyContainer.start();
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(CrappyResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test
    public void testBadRequest() {
        final WebTarget target = client.target(generateURL("/crappy/bad-request"));
        final Response resp = target.request().get();
        Assertions.assertEquals(400, resp.getStatus());
        Assertions.assertEquals("Everything you do is bad!", resp.readEntity(String.class));
    }

    @Path("/crappy")
    public static class CrappyResource {
        @GET
        @Path("/bad-request")
        @Produces("text/plain")
        public Response badRequest() {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Everything you do is bad!")
                    .build();
        }
    }
}
