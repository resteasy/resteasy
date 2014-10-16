package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import io.netty.channel.ChannelHandlerContext;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
      public String context(@Context ChannelHandlerContext context) {
          return context.channel().toString();
      }
      
      @POST
      @Path("/post")
      @Produces("text/plain")
      public String post(String postBody) {
          return postBody;
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
   public void testQuery() throws Exception
   {
      WebTarget target = client.target(generateURL("/query"));
      String val = target.queryParam("param", "val").request().get(String.class);
      Assert.assertEquals("val", val);
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
    public void testPost() {
      WebTarget target = client.target(generateURL("/post"));
      String postBody = "hello world";
      String result = (String) target.request().post(Entity.text(postBody), String.class);
      Assert.assertEquals(postBody, result);
    }
}
