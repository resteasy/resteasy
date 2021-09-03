package org.jboss.resteasy.test;

import org.hamcrest.CoreMatchers;
import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeadersLowerCasedTest
{
    private static final String RESPONSE_PREFIX = "headerNames: ";

    @Path("/headers")
    public static class Resource
    {
        @Context
        private HttpHeaders httpHeaders;

        @GET
        @Path("/lowercased")
        public String lowercased()
        {
            return RESPONSE_PREFIX +
                httpHeaders.getRequestHeaders()
                    .keySet()
                    .stream()
                    .collect(Collectors.joining(","));
        }

        @GET
        @Path("/lookup")
        public String lookup()
        {
            return RESPONSE_PREFIX +
                httpHeaders.getHeaderString("dUmMy-KeY");
        }
    }

    @BeforeClass
    public static void setup() throws Exception
    {
        ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
    }

    @AfterClass
    public static void end() throws Exception
    {
        ReactorNettyContainer.stop();
    }

    @Test
    public void testHeadersLowerCased() throws Exception
    {
        final Optional<String> maybeResp = mkCall("/headers/lowercased");

        Assert.assertTrue(maybeResp.isPresent());
        final String body = maybeResp.get();
        final String value = body.subSequence(RESPONSE_PREFIX.length(), body.length()).toString();
        MatcherAssert.assertThat(value, CoreMatchers.is("connection,dummy-key,host"));
    }

    @Test
    public void testCaseInsensitiveHeaderLookup() throws IOException {

        final Optional<String> maybeResp = mkCall("/headers/lookup");
        Assert.assertTrue(maybeResp.isPresent());
        final String body = maybeResp.get();
        final String headerValue = body.subSequence(RESPONSE_PREFIX.length(), body.length()).toString();
        MatcherAssert.assertThat(headerValue, CoreMatchers.is("dummyValue"));
    }

    private Optional<String> mkCall(final String path) throws IOException {
        try(Socket client=new Socket(TestPortProvider.getHost(),TestPortProvider.getPort())){
            try(PrintWriter out=new PrintWriter(client.getOutputStream(),true)){
                BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.printf("GET %s HTTP/1.1\r\n", path);
                out.print("Host: \r\n");
                out.print("Connection: close\r\n");
                out.print("DUMMY-KEY: dummyValue\r\n");
                out.print("\r\n");
                out.flush();

                final String statusLine=in.readLine();
                Assert.assertEquals("HTTP/1.1 200 OK",statusLine);

                final Optional<String> maybeResp=in.lines().filter(line->line.startsWith(RESPONSE_PREFIX)).findAny();
                client.close();
                return maybeResp;
            }
        }
    }
}