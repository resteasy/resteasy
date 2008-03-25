package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.spi.Dispatcher;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;

/**
 * Test POJO constructor/field injection.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructedInjectionTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   public static class ConstructedResource
   {
      UriInfo myInfo;
      String abs;

      public ConstructedResource(@Context UriInfo myInfo, @QueryParam("abs")String abs)
      {
         this.myInfo = myInfo;
         this.abs = abs;
      }

      @Path("/simple")
      @GET
      public String get()
      {
         System.out.println("abs query: " + abs);
         URI base = null;
         if (abs == null)
         {
            base = URI.create("http://localhost:8081/");
         }
         else
         {
            base = URI.create("http://localhost:8081/" + abs + "/");
         }

         System.out.println("BASE URI: " + myInfo.getBaseUri());
         System.out.println("Request URI: " + myInfo.getRequestUri());
         Assert.assertEquals(base.getPath(), myInfo.getBaseUri().getPath());
         Assert.assertEquals("/simple", myInfo.getPath());
         return "CONTENT";
      }

   }

   private void _test(HttpClient client, String uri)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

   @Test
   public void testUriInfo() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addResource(ConstructedResource.class);
         _test(new HttpClient(), "http://localhost:8081/simple");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

}