package org.jboss.resteasy.test.undertow;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.NewCookie.SameSite;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.cookies.NewCookie6265;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

/**
 * Unit test for NewCookie6265
 * 
 */
public class TestNewCookie6265 {

    private static UndertowJaxrsServer server;
    private static Client client;

    @Path("")
    public static class TestResource {

        @GET
        @Path("getNewCookie")
        public Response getNewCookie() throws Exception {
            NewCookie6265.Builder builder = new NewCookie6265.Builder("name");
            NewCookie6265 cookie = builder.value("value")
                    .version(17)
                    .path("/path")
                    .domain("domain")
                    .comment("comment")
                    .maxAge(23)
                    .expiry(new Date(3, 5, 7))
                    .secure(false)
                    .httpOnly(true)
                    .sameSite(SameSite.LAX)
                    .extension("a=b")
                    .extension("c")
                    .build();
            return Response.ok().cookie(cookie).build();
        }

        @GET
        @Path("checkCookie")
        public Response checkCookie(@CookieParam("name") String cookie) throws Exception {
            return Response.ok("value".equals(cookie)).build();
        }
    }

    @ApplicationPath("")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(TestResource.class);
            return classes;
        }
    }

    @BeforeAll
    public static void init() throws Exception {
        server = new UndertowJaxrsServer().start();
        server.deploy(MyApp.class);
        client = (ResteasyClient) ResteasyClientBuilder.newClient();
    }

    @AfterAll
    public static void stop() throws Exception {
        server.stop();
    }

    @Test
    public void testCookie6265() throws Exception {

        // Get NewCookie6265
        Response response = client.target("http://localhost:8081/getNewCookie").request().get();
        Assert.assertTrue(response.getStatus() == 200);
        NewCookie6265 newCookie6265 = (NewCookie6265) response.getCookies().get("name");
        Assert.assertTrue("value".equals(newCookie6265.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265.getVersion());
        Assert.assertTrue("/path".equals(newCookie6265.getPath()));
        Assert.assertTrue("domain".equals(newCookie6265.getDomain()));
        Assert.assertTrue(null == newCookie6265.getComment());
        Assert.assertTrue(23 == newCookie6265.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie6265.getExpiry()));
        Assert.assertFalse(newCookie6265.isSecure());
        Assert.assertTrue(newCookie6265.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie6265.getSameSite()));
        Assert.assertTrue(newCookie6265.getExtensions().size() == 2);
        Assert.assertTrue(newCookie6265.getExtensions().contains("a=b"));
        Assert.assertTrue(newCookie6265.getExtensions().contains("c"));

        // Send Cookie back to server
        response = client.target("http://localhost:8081/checkCookie").request().cookie(newCookie6265).get();
        Assert.assertTrue(response.readEntity(Boolean.class));
    }
}
