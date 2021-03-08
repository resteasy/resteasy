package org.jboss.resteasy.plugins.server.reactor.netty;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class BasicTest {
    private static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        ResteasyDeployment deployment = ReactorNettyContainer.start();
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
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
    public void pojo() {
        final WebTarget target = client.target(generateURL("/basic/pojo"));
        final Response resp = target.request().get();
        //System.out.println(resp.readEntity(String.class));
        assertEquals(42, resp.readEntity(Pojo.class).getAnswer());
    }

    public void sendBodyTest(final String method) {
        final String randomText = UUID.randomUUID().toString();
        final String resp =
            client.target(generateURL("/basic"))
                .request()
                .method(method, Entity.text(randomText), String.class);
        assertEquals(method.toUpperCase() + " " + randomText, resp);
    }

    @Path("/basic")
    public static class BasicResource {
        @GET
        public String get() {
            return "Hello world!";
        }

        @PUT
        public String post(String input) {
            return "PUT " + input;
        }

        @POST
        public String put(String input) {
            return "POST " + input;
        }

        @DELETE
        public String delete(String input) {
            return "DELETE " + input;
        }

        @PATCH
        public String patch(String input) {
            return "PATCH " + input;
        }

        @GET
        @Path("/pojo")
        @Produces(MediaType.APPLICATION_JSON)
        public Pojo pojo() {
            return new Pojo();
        }
    }

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

    public static class Pojo {
        private int answer = 42;

        public int getAnswer() {
            return answer;
        }

        public void setAnswer(int answer) {
            this.answer = answer;
        }
    }
}
