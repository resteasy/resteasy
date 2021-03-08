package org.jboss.resteasy.test;

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
import java.util.stream.Collectors;

/**
 * RESTEASY-2300
 * @author <a href="mailto:istudens@redhat.com">Ivo Studensky</a>
 */
public class HeaderEmptyHostTest
{
   @Path("/emptyhost")
   public static class Resource
   {
      @Context
      UriInfo uriInfo;

      @GET
      @Path("/test")
      public String hello()
      {
         return "uriInfo: " + uriInfo.getRequestUri().toString();
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
            final String uri = "/emptyhost/test";
            out.printf("GET %s HTTP/1.1\r\n", uri);
            out.print("Host: \r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.flush();
            String response = new BufferedReader(new InputStreamReader(client.getInputStream())).lines().collect(Collectors.joining("\n"));
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains("HTTP/1.1 200 OK"));
            // TODO I'm not sure that we have to convert this to unknown, but I guess we can verify if all adapters do that..
            //Assert.assertTrue(response.contains("uriInfo: http://unknown" + uri));
            Assert.assertTrue(response.contains("uriInfo: " + uri));
         }
      }
   }
}
