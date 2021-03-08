package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;

public class ClientBadRequestTest {
    private static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        ResteasyDeployment deployment = ReactorNettyContainer.start();
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(CrappyResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test
    public void testBadRequest() {
        final WebTarget target = client.target(generateURL("/crappy/bad-request"));
        final Response resp = target.request().get();
        assertEquals(400, resp.getStatus());
        assertEquals("Everything you do is bad!", resp.readEntity(String.class));
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
