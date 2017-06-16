package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Created by weinanli on 16/06/2017.
 */
public class SubresourceClassInjectionTest {
    public static class SubResource {

        String val;

        public SubResource(String val) {
            this.val = val;
        }

        @GET
        public String get() {
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

    @BeforeClass
    public static void setup() throws Exception {
        NettyContainer.start().getRegistry().addPerRequestResource(SubresourceClassInjectionTest.Resource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        NettyContainer.stop();
    }

    @Test
    public void testQuery() throws Exception
    {
        WebTarget target = client.target(generateURL("/sub/val"));
        String val = target.request().get(String.class);
        Assert.assertEquals("val", val);
    }
}
