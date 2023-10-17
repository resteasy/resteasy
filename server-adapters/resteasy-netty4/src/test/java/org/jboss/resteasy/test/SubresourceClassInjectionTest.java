package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by weinanli on 16/06/2017.
 */
public class SubresourceClassInjectionTest {
    public static class SubResource {

        public SubResource() {
        }

        @GET
        public String get(@PathParam("val") String val) {
            return val;
        }
    }

    @Path("/")
    public static class Resource {

        @Path("/sub/{val}")
        public Class<SubResource> sub2(@PathParam("val") String val) {
            return SubResource.class;
        }
    }

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        NettyContainer.start().getRegistry().addPerRequestResource(SubresourceClassInjectionTest.Resource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        NettyContainer.stop();
    }

    @Test
    public void testQuery() throws Exception {
        WebTarget target = client.target(generateURL("/sub/val"));
        String val = target.request().get(String.class);
        Assertions.assertEquals("val", val);
    }
}
