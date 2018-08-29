package org.jboss.resteasy.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Copy this class in a project using resteasy-netty or resteasy-netty4 and run the test.
 * The test will fail with Resteasy 3.0.8.Final, but pass with 3.0.6.Final.
 * It'll also pass if you remove the trailing slash in {@code generateURL("/test/")}.
 */
public class ResteasyTrailingSlashTest {
   private static NettyJaxrsServer server;

   @Path("/")
   public static class Resource {
      @GET
      @Path("/test/")
      @Produces(MediaType.TEXT_PLAIN)
      public String get() {
         return "hello world";
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("");
      server.setSecurityDomain(null);

      server.start();

      server.getDeployment().getRegistry().addPerRequestResource(Resource.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }

   @Test
   public void testTrailingSlash() throws Exception {
      Client client = ClientBuilder.newClient();
      String val = client.target(TestPortProvider.generateURL("/test/"))
// String val = client.target(TestPortProvider.generateURL("/test"))
              .request().get(String.class);
      assertEquals("hello world", val);
      client.close();
   }
}