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
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
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
            NewCookie6265.Builder builder6265 = new NewCookie6265.Builder("name6265");
            NewCookie6265 cookie6265 = builder6265.value("value1")
                    .version(17)
                    .path("/path1")
                    .domain("domain1")
                    .comment("comment1")
                    .maxAge(23)
                    .expiry(new Date(3, 5, 7))
                    .secure(false)
                    .httpOnly(true)
                    .sameSite(SameSite.LAX)
                    .extension("a=b")
                    .extension("c")
                    .build();
            NewCookie.Builder builder2109 = new NewCookie.Builder("name2109");
            NewCookie cookie2109 = builder2109.value("value2")
                    .version(19)
                    .path("/path2")
                    .domain("domain2")
                    .comment("comment2")
                    .maxAge(29)
                    .expiry(new Date(5, 7, 11))
                    .secure(true)
                    .httpOnly(false)
                    .sameSite(SameSite.STRICT)
                    .build();
            return Response.ok().cookie(cookie6265, cookie2109).build();
        }

        @GET
        @Path("checkCookie/cookie")
        public Response checkCookieCookie(
                @CookieParam("name6265") Cookie cookie6265,
                @CookieParam("name2109") Cookie cookie2109,
                @CookieParam("Domain") Cookie domain) throws Exception {
            boolean b6265 = "name6265".equals(cookie6265.getName()) &&
                    "value1".equals(cookie6265.getValue()) &&
                    cookie6265.getPath() == null &&
                    cookie6265.getDomain() == null;
            boolean b2109 = "name2109".equals(cookie2109.getName()) &&
                    "value2".equals(cookie2109.getValue()) &&
                    "/path2".equals(cookie2109.getPath()) &&
                    "domain2".equals(cookie2109.getDomain());
            boolean bDomain = domain == null;
            return Response.ok(b6265 && b2109 && bDomain).build();
        }

        @GET
        @Path("checkCookie/string")
        public Response checkCookieString(
                @CookieParam("name6265") Cookie cookie6265,
                @CookieParam("name2109") Cookie cookie2109,
                @CookieParam("Domain") Cookie domain) throws Exception {
            boolean b6265 = "name6265=value1".equals(cookie6265.toString());
            boolean b2109 = "name2109=value2; $Domain=domain2; $Path=/path2".equals(cookie2109.toString());
            boolean bDomain = domain == null;
            return Response.ok(b6265 && b2109 && bDomain).build();

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
    public void testCookies() throws Exception {

        // Get NewCookie6265
        Response response = client.target("http://localhost:8081/getNewCookie").request().get();
        Assert.assertTrue(response.getStatus() == 200);
        NewCookie6265 newCookie6265 = (NewCookie6265) response.getCookies().get("name6265");
        Assert.assertTrue("value1".equals(newCookie6265.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265.getVersion());
        Assert.assertTrue("/path1".equals(newCookie6265.getPath()));
        Assert.assertTrue("domain1".equals(newCookie6265.getDomain()));
        Assert.assertTrue(null == newCookie6265.getComment());
        Assert.assertTrue(23 == newCookie6265.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie6265.getExpiry()));
        Assert.assertFalse(newCookie6265.isSecure());
        Assert.assertTrue(newCookie6265.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie6265.getSameSite()));
        Assert.assertTrue(newCookie6265.getExtensions().size() == 2);
        Assert.assertTrue(newCookie6265.getExtensions().contains("a=b"));
        Assert.assertTrue(newCookie6265.getExtensions().contains("c"));

        NewCookie newCookie2109 = (NewCookie) response.getCookies().get("name2109");
        Assert.assertTrue("value2".equals(newCookie2109.getValue()));
        Assert.assertTrue(19 == newCookie2109.getVersion());
        Assert.assertTrue("/path2".equals(newCookie2109.getPath()));
        Assert.assertTrue("domain2".equals(newCookie2109.getDomain()));
        Assert.assertTrue("comment2".equals(newCookie2109.getComment()));
        Assert.assertTrue(29 == newCookie2109.getMaxAge());
        Assert.assertTrue(new Date(5, 7, 11).equals(newCookie2109.getExpiry()));
        Assert.assertTrue(newCookie2109.isSecure());
        Assert.assertFalse(newCookie2109.isHttpOnly());
        Assert.assertTrue(SameSite.STRICT.equals(newCookie2109.getSameSite()));

        // Send Cookie back to server
        response = client.target("http://localhost:8081/checkCookie/cookie").request()
                .cookie(newCookie6265)
                .cookie(newCookie2109)
                .get();
        Assert.assertTrue(response.readEntity(Boolean.class));

        response = client.target("http://localhost:8081/checkCookie/string").request()
                .cookie(newCookie6265)
                .cookie(newCookie2109)
                .get();
        Assert.assertTrue(response.readEntity(Boolean.class));
    }
}
