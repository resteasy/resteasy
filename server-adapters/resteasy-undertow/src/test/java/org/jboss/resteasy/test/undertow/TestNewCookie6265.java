package org.jboss.resteasy.test.undertow;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
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
 * Unit test for NewCookie6265 and NewCookie
 *
 */
public class TestNewCookie6265 {

    private static UndertowJaxrsServer server;
    private static Client client;

    @Path("")
    public static class TestResource {

        @GET
        @Path("getNewCookies/6265")
        public Response getNewCookies6265() throws Exception {
            NewCookie6265.Builder builder6265a = new NewCookie6265.Builder("name6265a");
            NewCookie6265 cookie6265a = builder6265a.value("value1")
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
            NewCookie6265.Builder builder6265b = new NewCookie6265.Builder("name6265b");
            NewCookie6265 cookie6265b = builder6265b.value("value2")
                    .version(19)
                    .path("/path2")
                    .domain("domain2")
                    .comment("comment2")
                    .maxAge(29)
                    .expiry(new Date(5, 7, 11))
                    .secure(true)
                    .httpOnly(false)
                    .sameSite(SameSite.STRICT)
                    .extension("d=e")
                    .extension("f")
                    .build();
            return Response.ok().cookie(cookie6265a, cookie6265b).build();
        }

        @GET
        @Path("getNewCookies/2109")
        public Response getNewCookies2109() throws Exception {
            NewCookie.Builder builder2109a = new NewCookie.Builder("name2109a");
            NewCookie cookie2109a = builder2109a.value("value1")
                    .version(17)
                    .path("/path1")
                    .domain("domain1")
                    .comment("comment1")
                    .maxAge(23)
                    .expiry(new Date(3, 5, 7))
                    .secure(false)
                    .httpOnly(true)
                    .sameSite(SameSite.LAX)
                    .build();
            NewCookie.Builder builder2109b = new NewCookie.Builder("name2109b");
            NewCookie cookie2109b = builder2109b.value("value2")
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
            return Response.ok().cookie(cookie2109a, cookie2109b).build();
        }

        @GET
        @Path("checkCookies/cookies/6265")
        public Response checkCookiesCookies6265(
                @CookieParam("name6265a") Cookie cookie6265a,
                @CookieParam("name6265b") Cookie cookie6265b,
                @CookieParam("Domain") Cookie domain) throws Exception {
            boolean b6265a = "name6265a=value1".equals(cookie6265a.toString());
            boolean b6265b = "name6265b=value2".equals(cookie6265b.toString());
            boolean bDomain = domain == null;
            return Response.ok(b6265a && b6265b && bDomain).build();
        }

        @GET
        @Path("checkCookies/string/6265")
        public Response checkCookiesString6265(
                @CookieParam("name6265a") String cookie6265a,
                @CookieParam("name6265b") String cookie6265b,
                @CookieParam("Domain") String domain) throws Exception {
            boolean b6265a = "value1".equals(cookie6265a);
            boolean b6265b = "value2".equals(cookie6265b);
            boolean bDomain = domain == null;
            return Response.ok(b6265a && b6265b && bDomain).build();
        }

        @GET
        @Path("checkCookies/cookies/2109")
        public Response checkCookiesCookies2109(
                @CookieParam("name2109a") Cookie cookie2109a,
                @CookieParam("name2109b") Cookie cookie2109b,
                @CookieParam("Domain") Cookie domain) throws Exception {
            boolean b2109a = "name2109a".equals(cookie2109a.getName()) &&
                    "value1".equals(cookie2109a.getValue()) &&
                    "/path1".equals(cookie2109a.getPath()) &&
                    "domain1".equals(cookie2109a.getDomain()) &&
                    "name2109a=value1; $Domain=domain1; $Path=/path1".equals(cookie2109a.toString());
            boolean b2109b = "name2109b".equals(cookie2109b.getName()) &&
                    "value2".equals(cookie2109b.getValue()) &&
                    "/path2".equals(cookie2109b.getPath()) &&
                    "domain2".equals(cookie2109b.getDomain()) &&
                    "name2109b=value2; $Domain=domain2; $Path=/path2".equals(cookie2109b.toString());
            boolean bDomain = domain == null;
            return Response.ok(b2109a && b2109b && bDomain).build();
        }

        @GET
        @Path("checkCookies/string/2109")
        public Response checkCookiesString2109(
                @CookieParam("name2109a") String cookie2109a,
                @CookieParam("name2109b") String cookie2109b,
                @CookieParam("Domain") String domain) throws Exception {
            boolean b2109a = "value1".equals(cookie2109a);
            boolean b2109b = "value2".equals(cookie2109b);
            boolean bDomain = domain == null;
            return Response.ok(b2109a && b2109b && bDomain).build();
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
    public void testCookies6265() throws Exception {

        Response response = client.target("http://localhost:8081/getNewCookies/6265").request().get();
        Assert.assertTrue(response.getStatus() == 200);
        Map<String, NewCookie> cookies = response.getCookies();
        NewCookie6265 newCookie6265a = (NewCookie6265) cookies.get("name6265a");
        Assert.assertTrue("value1".equals(newCookie6265a.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265a.getVersion());
        Assert.assertTrue("/path1".equals(newCookie6265a.getPath()));
        Assert.assertTrue("domain1".equals(newCookie6265a.getDomain()));
        Assert.assertTrue(null == newCookie6265a.getComment());
        Assert.assertTrue(23 == newCookie6265a.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie6265a.getExpiry()));
        Assert.assertFalse(newCookie6265a.isSecure());
        Assert.assertTrue(newCookie6265a.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie6265a.getSameSite()));
        Assert.assertTrue(newCookie6265a.getExtensions().size() == 2);
        Assert.assertTrue(newCookie6265a.getExtensions().contains("a=b"));
        Assert.assertTrue(newCookie6265a.getExtensions().contains("c"));

        NewCookie newCookie6265b = cookies.get("name6265b");
        Assert.assertTrue("value2".equals(newCookie6265b.getValue()));
        Assert.assertTrue(NewCookie6265.NO_VERSION == newCookie6265b.getVersion());
        Assert.assertTrue("/path2".equals(newCookie6265b.getPath()));
        Assert.assertTrue("domain2".equals(newCookie6265b.getDomain()));
        Assert.assertTrue(null == newCookie6265b.getComment());
        Assert.assertTrue(29 == newCookie6265b.getMaxAge());
        Assert.assertTrue(new Date(5, 7, 11).equals(newCookie6265b.getExpiry()));
        Assert.assertTrue(newCookie6265b.isSecure());
        Assert.assertFalse(newCookie6265b.isHttpOnly());
        Assert.assertTrue(SameSite.STRICT.equals(newCookie6265b.getSameSite()));

        // Send Cookie back to server
        response = client.target("http://localhost:8081/checkCookies/cookies/6265").request()
                .cookie(newCookie6265a)
                .cookie(newCookie6265b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));

        response = client.target("http://localhost:8081/checkCookies/string/6265").request()
                .cookie(newCookie6265a)
                .cookie(newCookie6265b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));
    }

    @Test
    public void testCookies2109() throws Exception {

        Response response = client.target("http://localhost:8081/getNewCookies/2109").request().get();
        Assert.assertTrue(response.getStatus() == 200);
        Map<String, NewCookie> cookies = response.getCookies();
        NewCookie newCookie2109a = cookies.get("name2109a");
        Assert.assertTrue("value1".equals(newCookie2109a.getValue()));
        Assert.assertTrue(17 == newCookie2109a.getVersion());
        Assert.assertTrue("/path1".equals(newCookie2109a.getPath()));
        Assert.assertTrue("domain1".equals(newCookie2109a.getDomain()));
        Assert.assertTrue("comment1".equals(newCookie2109a.getComment()));
        Assert.assertTrue(23 == newCookie2109a.getMaxAge());
        Assert.assertTrue(new Date(3, 5, 7).equals(newCookie2109a.getExpiry()));
        Assert.assertFalse(newCookie2109a.isSecure());
        Assert.assertTrue(newCookie2109a.isHttpOnly());
        Assert.assertTrue(SameSite.LAX.equals(newCookie2109a.getSameSite()));

        NewCookie newCookie2109b = cookies.get("name2109b");
        Assert.assertTrue("value2".equals(newCookie2109b.getValue()));
        Assert.assertTrue(19 == newCookie2109b.getVersion());
        Assert.assertTrue("/path2".equals(newCookie2109b.getPath()));
        Assert.assertTrue("domain2".equals(newCookie2109b.getDomain()));
        Assert.assertTrue("comment2".equals(newCookie2109b.getComment()));
        Assert.assertTrue(29 == newCookie2109b.getMaxAge());
        Assert.assertTrue(new Date(5, 7, 11).equals(newCookie2109b.getExpiry()));
        Assert.assertTrue(newCookie2109b.isSecure());
        Assert.assertFalse(newCookie2109b.isHttpOnly());
        Assert.assertTrue(SameSite.STRICT.equals(newCookie2109b.getSameSite()));

        // Send Cookie back to server
        response = client.target("http://localhost:8081/checkCookies/cookies/2109").request()
                .cookie(newCookie2109a)
                .cookie(newCookie2109b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));

        response = client.target("http://localhost:8081/checkCookies/string/2109").request()
                .cookie(newCookie2109a)
                .cookie(newCookie2109b)
                .get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.readEntity(Boolean.class));
    }
}
