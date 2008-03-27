package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.server.resourcefactory.SingletonResource;
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
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriInfoTest
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

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @GET
      public String get(@Context UriInfo info, @QueryParam("abs")String abs)
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

         System.out.println("BASE URI: " + info.getBaseUri());
         System.out.println("Request URI: " + info.getRequestUri());
         Assert.assertEquals(base.getPath(), info.getBaseUri().getPath());
         Assert.assertEquals("/simple", info.getPath());
         return "CONTENT";
      }

      @Context
      UriInfo myInfo;

      @Path("/simple/fromField")
      @GET
      public String get(@QueryParam("abs")String abs)
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
         Assert.assertEquals("/simple/fromField", myInfo.getPath());
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
   public void testUriInfoWithSingleton() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addResourceFactory(new SingletonResource(new SimpleResource()));
         _test(new HttpClient(), "http://localhost:8081/simple/fromField");
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

   @Test
   public void testUriInfo() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addResource(SimpleResource.class);
         _test(new HttpClient(), "http://localhost:8081/simple");
         _test(new HttpClient(), "http://localhost:8081/simple/fromField");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Test
   public void testUriInfo2() throws Exception
   {
      dispatcher = EmbeddedContainer.start("/resteasy");
      try
      {
         dispatcher.getRegistry().addResource(SimpleResource.class);
         _test(new HttpClient(), "http://localhost:8081/resteasy/simple?abs=resteasy");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }


}
