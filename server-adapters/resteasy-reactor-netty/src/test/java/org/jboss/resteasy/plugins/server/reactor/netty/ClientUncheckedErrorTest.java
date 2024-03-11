package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Random;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ClientUncheckedErrorTest {

    private static Client client;
    private static ResteasyDeployment deployment;

    @BeforeAll
    public static void setup() throws Exception {
        deployment = ReactorNettyContainer.start();
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(MyResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Test
    public void testClientUncheckedException() {
        final WebTarget target = client.target(generateURL("/resource/out-of-bounds/" + new Random().nextInt()));
        final Response resp = target.request().get();
        Assertions.assertEquals(500, resp.getStatus());
        Assertions.assertEquals("Index 0 out of bounds for length 0", resp.readEntity(String.class));
    }

    @Test
    public void testClientUncheckedMappedException() {
        deployment.getProviderFactory().registerProviderInstance(new MyExceptionMapper());
        final WebTarget target = client.target(generateURL("/resource/out-of-bounds/" + new Random().nextInt()));
        final Response resp = target.request().get();
        Assertions.assertEquals(202, resp.getStatus());
        Assertions.assertEquals("Try again later", resp.readEntity(String.class));

    }

    @Path("/resource")
    public static class MyResource {
        @GET
        @Path("/out-of-bounds/{index}")
        @Produces("text/plain")
        public Response outOfBounds(@PathParam("index") Integer index) {

            int[] arr = {};
            return Response.ok("Value at index " + index + " is " + arr[0]).build();

        }
    }

    @Provider
    public class MyExceptionMapper implements ExceptionMapper<ArrayIndexOutOfBoundsException> {

        @Override
        public Response toResponse(ArrayIndexOutOfBoundsException ex) {
            return Response.status(202)
                    .entity("Try again later")
                    .build();
        }
    }

}
