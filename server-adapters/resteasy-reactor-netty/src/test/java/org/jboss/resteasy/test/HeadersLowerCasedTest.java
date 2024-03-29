package org.jboss.resteasy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HeadersLowerCasedTest {
    private static final String RESPONSE_PREFIX = "headerNames: ";

    @Path("/headers")
    public static class Resource {
        @Context
        private HttpHeaders httpHeaders;

        @GET
        @Path("/lowercased")
        public String lowercased() {
            return RESPONSE_PREFIX +
                    httpHeaders.getRequestHeaders()
                            .keySet()
                            .stream()
                            .collect(Collectors.joining(","));
        }

        @GET
        @Path("/lookup")
        public String lookup() {
            return RESPONSE_PREFIX +
                    httpHeaders.getHeaderString("dUmMy-KeY");
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
    }

    @AfterAll
    public static void end() throws Exception {
        ReactorNettyContainer.stop();
    }

    @Test
    public void testHeadersLowerCased() throws Exception {
        final Optional<String> maybeResp = mkCall("/headers/lowercased");

        Assertions.assertTrue(maybeResp.isPresent());
        final String body = maybeResp.get();
        final String value = body.subSequence(RESPONSE_PREFIX.length(), body.length()).toString();
        Assertions.assertEquals("connection,dummy-key,host", value);
    }

    @Test
    public void testCaseInsensitiveHeaderLookup() throws IOException {

        final Optional<String> maybeResp = mkCall("/headers/lookup");
        Assertions.assertTrue(maybeResp.isPresent());
        final String body = maybeResp.get();
        final String headerValue = body.subSequence(RESPONSE_PREFIX.length(), body.length()).toString();
        Assertions.assertEquals("dummyValue", headerValue);
    }

    private Optional<String> mkCall(final String path) throws IOException {
        try (Socket client = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.printf("GET %s HTTP/1.1\r\n", path);
                out.print(String.format("Host: %s\r\n", TestPortProvider.getHost()));
                out.print("Connection: close\r\n");
                out.print("DUMMY-KEY: dummyValue\r\n");
                out.print("\r\n");
                out.flush();

                final String statusLine = in.readLine();
                Assertions.assertEquals("HTTP/1.1 200 OK", statusLine);

                final Optional<String> maybeResp = in.lines().filter(line -> line.startsWith(RESPONSE_PREFIX)).findAny();
                client.close();
                return maybeResp;
            }
        }
    }
}
