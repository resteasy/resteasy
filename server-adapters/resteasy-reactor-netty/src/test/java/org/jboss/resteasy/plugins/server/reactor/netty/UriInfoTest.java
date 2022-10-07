package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.hamcrest.CoreMatchers;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UriInfoTest {

    private static final String RESPONSE_PREFIX = "uriInfo: ";

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
    public void testUriInfoUsingFullUriWithHostname() throws Exception
    {
        final String uri = TestPortProvider.generateURL("/uriinfo");
        Assert.assertEquals(uri, test(uri));
    }

    @Test
    public void testUriInfoUsingFullUriWithIp() throws Exception
    {
        final String uri = TestPortProvider.generateURL("/uriinfo").replace("localhost", "127.0.0.1");
        Assert.assertEquals(uri, test(uri));
    }

    @Test
    public void testUriInfoUsingPartialUri() throws Exception
    {
        final String uri = "/uriinfo";

        final String response = test(uri);

        final String absoluteUri = TestPortProvider.generateURL(uri);
        assertThat(response, CoreMatchers.either(CoreMatchers.is(absoluteUri))
                .or(CoreMatchers.is(TestPortProvider.getHost())));
    }

    private String test(final String uri) throws UnknownHostException, IOException {

        try (Socket client = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
               BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
               out.printf("GET %s HTTP/1.1\r\n", uri);
               out.print("Host: \r\n");
               out.print("Connection: close\r\n");
               out.print("\r\n");
               out.flush();

               final String statusLine = in.readLine();
               Assert.assertEquals("HTTP/1.1 200 OK", statusLine);

               final Optional<String> maybeUriInfoResp = in.lines().filter(line -> line.startsWith(RESPONSE_PREFIX)).findAny();

               client.close();

               Assert.assertTrue(maybeUriInfoResp.isPresent());
               final String uriInfoResp = maybeUriInfoResp.get();

               return uriInfoResp.subSequence(RESPONSE_PREFIX.length(), uriInfoResp.length()).toString();
             }
         }

    }

    @Path("/uriinfo")
    public static class Resource
    {
       @Context
       UriInfo uriInfo;

       @Context
       ResteasyUriInfo resuriInfo;

       @GET
       public String echoUriInfo()
       {
          return RESPONSE_PREFIX + uriInfo.getRequestUri().toString();
       }

    }


}
