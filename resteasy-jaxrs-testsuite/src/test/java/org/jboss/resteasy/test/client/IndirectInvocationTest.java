package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collection;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Test to create Invocations object without invoking them directly.
 *
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 */
public class IndirectInvocationTest extends BaseResourceTest {

    public static final int REPEAT = 15;

    @Path("/test")
    public static class TestResource
    {
        @GET
        @Path("/query")
        @Produces("text/plain")
        public String get(@QueryParam("param") String p, @QueryParam("id") String id) {
            return p + " " + id;
        }

        @POST
        @Path("/send")
        @Consumes("text/plain")
        public String post(@QueryParam("param") String p, @QueryParam("id") String id, String str) {
            return str;
        }
    }

    static Client client;

    @BeforeClass
    public static void setupClient()
    {
        client = ClientBuilder.newClient();
        addPerRequestResource(TestResource.class);

    }

    @AfterClass
    public static void close()
    {
        client.close();
    }


    /*
     * Create Invocation request and submit it using invoke() method, verify the answer.
     */
    @Test
    public void invokeLaterTest() {
        Invocation inv = client.target(generateURL("/") + "test/query")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildGet();

        Response response = inv.invoke();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("123456 3", response.readEntity(String.class));
    }

    /*
     * Create two Invocations requests, store them in the list and then call them multiple times
     * https://weblogs.java.net/blog/spericas/archive/2011/10/20/jax-rs-20-client-api-generic-interface
     */
    @Test
    public void invokeMultipleTimes() {
        Invocation inv1 = client.target(generateURL("/") + "test/query")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildGet();

        Invocation inv2 = client.target(generateURL("/") + "test/send")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildPost(Entity.text("50.0"));


        Collection<Invocation> invs = Arrays.asList(inv1, inv2);

        for (int i = 0; i < REPEAT; i++) {
            for (Invocation inv : invs) {
                Response response = inv.invoke();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            }
        }
    }

}
