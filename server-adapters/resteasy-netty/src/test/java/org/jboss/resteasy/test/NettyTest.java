package org.jboss.resteasy.test;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

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
      @Path("/exception")
      @Produces("text/plain")
      public String exception() {
         throw new RuntimeException();
      }

      @GET
      @Path("/context")
      @Produces("text/plain")
      public String context(@Context ChannelHandlerContext context) {
          return context.getChannel().toString();
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
   }

   @AfterClass
   public static void end() throws Exception
   {
      NettyContainer.stop();
   }

   @Test
   public void testBasic() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/test"));
      String val = target.request().get(String.class);
      Assert.assertEquals("hello world", val);
   }

   @Test
   public void testHeadContentLength() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/test"));
      Response getResponse = target.request().buildGet().invoke();
      String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
      Assert.assertEquals("hello world", val);
      Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
      Assert.assertEquals("HEAD method should return the same Content-Length as the GET method", getResponse.getLength(), headResponse.getLength());
      Assert.assertTrue(getResponse.getLength() > 0);
   }

   @Test
   public void testUnhandledException() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/exception"));
      Response resp = target.request().get();
      Assert.assertEquals(500, resp.getStatus());
   }

    @Test
    public void testChannelContext() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/context"));
      String val = target.request().get(String.class);
      Assert.assertNotNull(val);
      Assert.assertFalse(val.isEmpty());
    }
}
