package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import reactor.netty.http.server.HttpServerRequest;

public class UriInfoTest {

    private static final String RESPONSE_PREFIX = "uriInfo: ";

    @BeforeAll
    public static void setup() throws Exception {
        ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
    }

    @AfterAll
    public static void end() throws Exception {
        ReactorNettyContainer.stop();
    }

    @Test
    public void testUriInfoUsingFullUriWithHostname() throws Exception {
        final String uri = TestPortProvider.generateURL("/uriinfo");
        Assertions.assertEquals(uri, uriInfoRequestUri(uri));
    }

    @Test
    public void testUriInfoUsingFullUriWithIp() throws Exception {
        final String uri = TestPortProvider.generateURL("/uriinfo").replace("localhost", "127.0.0.1");
        Assertions.assertEquals(uri, uriInfoRequestUri(uri));
    }

    @Test
    public void testUriInfoUsingPartialUri() throws Exception {
        final String uri = "/uriinfo";
        final String response = uriInfoRequestUri(uri);
        final String absoluteUri = TestPortProvider.generateURL(uri);
        Assertions.assertEquals(absoluteUri, response);
    }

    @Test
    public void testNoHostHeader() throws Exception {
        testProblematicHostHeader(Collections.emptyMap());
    }

    @Test
    @Disabled("https://github.com/reactor/reactor-netty/issues/2723")
    public void testEmptyStringHostHeader() throws Exception {
        final Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Host", "");
        testProblematicHostHeader(additionalHeaders);
    }

    @Test
    @Disabled("https://github.com/reactor/reactor-netty/issues/2723")
    public void testOnlySpacesHostHeader() throws Exception {
        final Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Host", "                  ");
        testProblematicHostHeader(additionalHeaders);
    }

    @Test
    public void testCannotDetermineHost() {
        final ReactorNettyJaxrsServer.UriExtractor extractor = new ReactorNettyJaxrsServer.UriExtractor();
        final HttpServerRequest req = mock(HttpServerRequest.class, RETURNS_MOCKS);
        when(req.hostAddress()).thenReturn(null);
        try {
            extractor.extract(req, "/contextPath");
            Assertions.fail("Inability to determine a host address should have thrown an IllegalArgumentException.");
        } catch (final IllegalArgumentException ignore) {
        } catch (final Exception e) {
            Assertions.fail("Inability to determine a host address should have thrown an IllegalArgumentException.");
        }
    }

    /**
     * Meant to test cases related to a problematic request Host header (no Host header, empty Host header, etc).
     *
     * @param hostHeader
     */
    private void testProblematicHostHeader(final Map<String, String> hostHeader) throws IOException {
        // Optional<Tuple2<String, String>> is the best 'design' data structure, but not going there for this test..
        final String uri = "/uriinfo";
        final String response = uriInfoRequestUri(uri, hostHeader);
        final String expectedUri = "http://127.0.0.1:" + TestPortProvider.getPort() + uri;
        Assertions.assertEquals(expectedUri, response);
    }

    /**
     * @see #uriInfoRequestUri(String, Map<String, String>) always sends the Host header in the request.
     */
    private String uriInfoRequestUri(final String uri) throws IOException {
        final Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Host", TestPortProvider.getHost() + ":" + TestPortProvider.getPort());
        return uriInfoRequestUri(uri, additionalHeaders);

    }

    /**
     *
     * Returns what the server saw in {@link UriInfo#getRequestUri()}.
     *
     * @param uri
     * @param additionalHeaders added to the request
     * @return
     * @throws IOException
     */
    private String uriInfoRequestUri(final String uri, Map<String, String> additionalHeaders) throws IOException {
        try (Socket client = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.printf("GET %s HTTP/1.1\r\n", uri);
                additionalHeaders.forEach((n, v) -> out.printf("%s: %s\r\n", n, v));
                out.print("Connection: close\r\n");
                out.print("\r\n");
                out.flush();

                final String statusLine = in.readLine();
                Assertions.assertEquals("HTTP/1.1 200 OK", statusLine);

                final Optional<String> maybeUriInfoResp = in.lines().filter(line -> line.startsWith(RESPONSE_PREFIX)).findAny();

                client.close();

                Assertions.assertTrue(maybeUriInfoResp.isPresent());
                final String uriInfoResp = maybeUriInfoResp.get();

                return uriInfoResp.subSequence(RESPONSE_PREFIX.length(), uriInfoResp.length()).toString();
            }
        }

    }

    @Path("/uriinfo")
    public static class Resource {
        @Context
        UriInfo uriInfo;

        @Context
        ResteasyUriInfo resuriInfo;

        @GET
        public String echoUriInfo() {
            return RESPONSE_PREFIX + uriInfo.getRequestUri().toString();
        }

    }
}
