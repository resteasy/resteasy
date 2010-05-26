package org.jboss.resteasy.test.finegrain.client;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import junit.framework.Assert;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResponseObjectTest
{
   @Path("test")
   static interface ResponseObjectClient
   {
      @GET
      MyObject get();
   }

   @Path("test")
   public static class ResponseObjectResource
   {

      @GET
      @Produces("text/plain")
      public String get()
      {
         return "ABC";
      }
   }

   @ResponseObject
   public static interface MyObject
   {
      @Status
      int status();

      @Body
      String body();
      
      ClientResponse response();
      
      @HeaderParam("Content-Type")
      String contentType();
   }

   private static InMemoryClientExecutor executor;
   private static ResponseObjectClient client;

   @BeforeClass
   public static void setup()
   {
      executor = new InMemoryClientExecutor();
      executor.getRegistry().addPerRequestResource(ResponseObjectResource.class);
      client = ProxyFactory.create(ResponseObjectClient.class, "", executor);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSimple()
   {
      MyObject obj = client.get();
      Assert.assertEquals(200, obj.status());
      Assert.assertEquals("ABC", obj.body());
      Assert.assertEquals("text/plain", obj.response().getHeaders().getFirst("Content-Type"));
      Assert.assertEquals("text/plain", obj.contentType());
   }
}
