package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;
import java.util.regex.Pattern;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.StringContextReplacement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VertxTest {

    @Path("/")
    public static class Resource {
        @GET
        @Path("/test")
        @Produces("text/plain")
        public String hello() {
            return "hello world";
        }

        @GET
        @Path("empty")
        public void empty() {

        }

        @GET
        @Path("query")
        public String query(@QueryParam("param") String value) {
            return value;

        }

        @GET
        @Path("/exception")
        @Produces("text/plain")
        public String exception() {
            throw new RuntimeException();
        }

        @GET
        @Path("large")
        @Produces("text/plain")
        public String large() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < 1000; i++) {
                buf.append(i);
            }
            return buf.toString();
        }

        @GET
        @Path("/context")
        @Produces("text/plain")
        public String context(
                @Context io.vertx.core.Context context,
                @Context io.vertx.core.Vertx vertx,
                @Context io.vertx.core.http.HttpServerRequest req,
                @Context io.vertx.core.http.HttpServerResponse resp) {
            if (context != null && vertx != null && req != null && resp != null) {
                return "pass";
            } else {
                return "fail";
            }
        }

        @POST
        @Path("/post")
        @Produces("text/plain")
        public String post(String postBody) {
            return postBody;
        }

        @GET
        @Path("/test/absolute")
        @Produces("text/plain")
        public String absolute(@Context UriInfo info) {
            return "uri: " + info.getRequestUri().toString();
        }

        @POST
        @Path("/replace")
        @Produces("text/plain")
        @Consumes("text/plain")
        public String replace(String replace) {
            return StringContextReplacement.replace(replace);
        }

        @GET
        @Path("request")
        @Produces("text/plain")
        public String getRequest(@Context HttpRequest req) {
            return req.getRemoteAddress() + "/" + req.getRemoteHost();
        }
    }

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        VertxContainer.start().getRegistry().addPerRequestResource(Resource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        VertxContainer.stop();
    }

    @Test
    public void testBasic() throws Exception {
        WebTarget target = client.target(generateURL("/test"));
        String val = target.request().get(String.class);
        Assertions.assertEquals("hello world", val);
    }

    @Test
    public void testHeadContentLength() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test"));
        Response getResponse = target.request().buildGet().invoke();
        String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
        Assertions.assertEquals("hello world", val);
        Assertions.assertEquals("chunked", getResponse.getHeaderString("transfer-encoding"));
        Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
        Assertions.assertNull(headResponse.getHeaderString("Content-Length"));
        Assertions.assertNull(headResponse.getHeaderString("transfer-encoding"));
    }

    @Test
    public void testQuery() throws Exception {
        WebTarget target = client.target(generateURL("/query"));
        String val = target.queryParam("param", "val").request().get(String.class);
        Assertions.assertEquals("val", val);
    }

    @Test
    public void testEmpty() throws Exception {
        WebTarget target = client.target(generateURL("/empty"));
        Response response = target.request().get();
        try {
            Assertions.assertEquals(204, response.getStatus());
        } finally {
            response.close();
        }
    }

    @Test
    public void testLarge() throws Exception {
        WebTarget target = client.target(generateURL("/large"));
        Response response = target.request().get();
        try {
            Assertions.assertEquals(200, response.getStatus());
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < 1000; i++) {
                buf.append(i);
            }
            String expected = buf.toString();
            String have = response.readEntity(String.class);
            Assertions.assertEquals(expected, have);

        } finally {
            response.close();
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        WebTarget target = client.target(generateURL("/exception"));
        Response resp = target.request().get();
        try {
            Assertions.assertEquals(500, resp.getStatus());
        } finally {
            resp.close();
        }
    }

    @Test
    public void testChannelContext() throws Exception {
        WebTarget target = client.target(generateURL("/context"));
        String val = target.request().get(String.class);
        Assertions.assertEquals("pass", val);
    }

    @Test
    public void testReplacement() throws Exception {
        // this test was put in to make sure that without servlet it still works.
        WebTarget target = client.target(generateURL("/replace"));
        String val = target.request().post(Entity.text("${contextpath}"), String.class);
        Assertions.assertEquals("", val);
    }

    @Test
    public void testPost() {
        WebTarget target = client.target(generateURL("/post"));
        String postBody = "hello world";
        String result = (String) target.request().post(Entity.text(postBody), String.class);
        Assertions.assertEquals(postBody, result);
    }

    /**
     * Per the HTTP spec, we must allow requests like:
     * <p>
     *
     * <pre>
     *     GET http://www.example.com/content HTTP/1.1
     *     Host: www.example.com
     * </pre>
     * <p>
     * <blockquote>
     * RFC 2616 5.1.12:
     * To allow for transition to absoluteURIs in all requests in future
     * versions of HTTP, all HTTP/1.1 servers MUST accept the absoluteURI
     * form in requests, even though HTTP/1.1 clients will only generate
     * them in requests to proxies.
     * </blockquote>
     *
     * @throws Exception
     */
    @Test
    public void testAbsoluteURI() throws Exception {
        String uri = generateURL("/test/absolute");

        Socket client = new Socket(getHost(), getPort());
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out.printf(Locale.US, "GET %s HTTP/1.1\r\nHost: %s:%d\r\n\r\n", uri, getHost(), getPort());
        String statusLine = in.readLine();
        String response = in.readLine();
        while (!response.startsWith("uri")) {
            response = in.readLine();
        }
        client.close();
        Assertions.assertEquals("HTTP/1.1 200 OK", statusLine);
        Assertions.assertEquals(uri, response.subSequence(5, response.length()));
    }

    @Test
    public void testRequest() throws Exception {
        WebTarget target = client.target(generateURL("/request"));
        String val = target.request().get(String.class);
        final String pattern = "^127.0.0.1/.+";
        Assertions.assertTrue(Pattern.matches(pattern, val),
                String.format("Expected value '%s' to match pattern '%s'", val, pattern));
    }
}
