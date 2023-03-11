package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 *         RESTEASY-1244
 *
 * @version $Revision: 1 $
 */
public class HeaderTooLongTest {
    static String longString = "abcdefghijklmnopqrstuvwxyz";
    static {
        for (int i = 0; i < 10; i++) {
            longString += longString;
        }
    }

    @Path("/")
    public static class Resource {
        @GET
        @Path("/test")
        public String hello(@Context HttpHeaders headers) {
            return "hello world";
        }
    }

    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        NettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() throws Exception {
        client.close();
        NettyContainer.stop();
    }

    @Test
    public void testLongHeader() throws Exception {
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().header("xheader", longString).get();
        Assert.assertEquals(400, response.getStatus());
    }
}
