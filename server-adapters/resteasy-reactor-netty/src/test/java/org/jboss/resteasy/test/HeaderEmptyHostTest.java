package org.jboss.resteasy.test;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.CoreMatchers;
import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

/**
 * RESTEASY-2300
 * @author <a href="mailto:istudens@redhat.com">Ivo Studensky</a>
 */
public class HeaderEmptyHostTest
{
   private static final String RESPONSE_PREFIX = "uriInfo: ";

   @Path("/emptyhost")
   public static class Resource
   {
      @Context
      UriInfo uriInfo;

      @GET
      @Path("/test")
      public String hello()
      {
         return RESPONSE_PREFIX + uriInfo.getRequestUri().toString();
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
   public void testEmptyHostHeader() throws Exception
   {
      try (Socket client = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
         try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            final String uri = "/emptyhost/test";
            out.printf("GET %s HTTP/1.1\r\n", uri);
            out.print("Host: \r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.flush();

            final String statusLine = in.readLine();
            Assert.assertEquals("HTTP/1.1 200 OK", statusLine);

            final Optional<String> maybeResp = in.lines().filter(line -> line.startsWith(RESPONSE_PREFIX)).findAny();

            client.close();

            Assert.assertTrue(maybeResp.isPresent());
            final String response = maybeResp.get();

            final String actualAbsoluteUri = response.subSequence(RESPONSE_PREFIX.length(), response.length()).toString();

            final String expectedAbsoluteUri = TestPortProvider.generateURL(uri);
            assertThat(actualAbsoluteUri, either(CoreMatchers.is(expectedAbsoluteUri))
                    .or(CoreMatchers.is(expectedAbsoluteUri.replace(TestPortProvider.getHost(), "127.0.0.1"))));

            }
      }
   }

}
