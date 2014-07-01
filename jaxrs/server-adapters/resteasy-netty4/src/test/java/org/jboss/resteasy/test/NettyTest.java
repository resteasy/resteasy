package org.jboss.resteasy.test;

import io.netty.channel.ChannelHandlerContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettyTest
{
   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/test")
      @Produces("text/plain")
      public String hello()
      {
         return "hello world";
      }

      @GET
      @Path("/echo/")
      @Produces("text/plain")
      public String hello(@QueryParam("text") String input)
      {
         return input+input;
      }

      @GET
      @Path("/ping")
      public Response pingRoot(@Context UriInfo uriInfo, @QueryParam("text") String input) {
         if (uriInfo.getPath().endsWith("/")) {
            return ping();
         } else {
            return Response
                    .status(Response.Status.MOVED_PERMANENTLY)
                    .build();
         }
      }

      @GET
      @Path("/ping/index.html")
      @Produces("text/plain")
      public Response ping()
      {
         return Response
                 .status(Response.Status.OK)
                 .entity("PONG")
                 .build();
      }

      @GET
      @Path("empty")
      public void empty() {

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
      public String context(@Context ChannelHandlerContext context) {
          return context.channel().toString();
      }
   }

   static Client client;
   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      try
      {
         client.close();
      }
      catch (Exception e)
      {

      }
      NettyContainer.stop();
   }

   @Test
   public void testBasic() throws Exception
   {
      WebTarget target = client.target(generateURL("/test"));
      String val = target.request().get(String.class);
      Assert.assertEquals("hello world", val);
   }

   @Test
   public void testEmpty() throws Exception
   {
      WebTarget target = client.target(generateURL("/empty"));
      Response response = target.request().get();
      try
      {
         Assert.assertEquals(204, response.getStatus());
      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testLarge() throws Exception
   {
      WebTarget target = client.target(generateURL("/large"));
      Response response = target.request().get();
      try
      {
         Assert.assertEquals(200, response.getStatus());
         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < 1000; i++) {
            buf.append(i);
         }
         String expected = buf.toString();
         String have = response.readEntity(String.class);
         Assert.assertEquals(expected, have);

      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testUnhandledException() throws Exception
   {
      WebTarget target = client.target(generateURL("/exception"));
      Response resp = target.request().get();
      try
      {
         Assert.assertEquals(500, resp.getStatus());
      }
      finally
      {
         resp.close();
      }
   }

    @Test
    public void testChannelContext() throws Exception {
        WebTarget target = client.target(generateURL("/context"));
        String val = target.request().get(String.class);
        Assert.assertNotNull(val);
        Assert.assertFalse(val.isEmpty());
    }

   @Test
   public void testContextPathNoTrailingSlash() throws Exception {
      WebTarget target = client.target(generateURL("/ping"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), resp.getStatus());
      } finally {
         resp.close();
      }
   }

   @Test
   public void testContextPathTrailingSlash() throws Exception {
      WebTarget target = client.target(generateURL("/ping/"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
         Assert.assertEquals("PONG", resp.readEntity(String.class));
      } finally {
         resp.close();
      }
   }

   @Test
   public void testSpecifyFileInContextPath() throws Exception {
      WebTarget target = client.target(generateURL("/ping/index.html"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
         Assert.assertEquals("PONG", resp.readEntity(String.class));
      } finally {
         resp.close();
      }
   }

   /**
    * https://issues.jboss.org/browse/RESTEASY-1077
    */
   @Test
   public void testStuff() throws Exception {
      WebTarget target = client.target(generateURL("/test/"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
      } finally {
         resp.close();
      }
   }
   @Test
   public void testTrailingSlashWithParams() throws Exception {
      WebTarget target = client.target(generateURL("/echo/?text=Test"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
      } finally {
         resp.close();
      }
   }

   /**
    * https://issues.jboss.org/browse/RESTEASY-1077
    */
   @Test
   public void testNoTrailingSlashWithParams() throws Exception {
      WebTarget target = client.target(generateURL("/echo?text=Test"));
      Response resp = target.request().get();
      try {
         Assert.assertEquals(200, resp.getStatus());
         Assert.assertEquals("TestTest", resp.readEntity(String.class));
      } finally {
         resp.close();
      }
   }

    /**
     * https://issues.jboss.org/browse/RESTEASY-1077
     */
    @Test
    public void testNoTrailingSlash() throws Exception {
        WebTarget target = client.target(generateURL("/test"));
        Response resp = target.request().get();

        try {
            Assert.assertEquals(200, resp.getStatus());
        } finally {
            resp.close();
        }
    }
}