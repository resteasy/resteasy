package org.jboss.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathLimitedTest
{
   private static Dispatcher dispatcher;

   @Path("/unlimited{param:.*}")
   public static class UnlimitedOnPathResource
   {
      @GET
      public String hello()
      {
         return "hello world";
      }
   }

   @Path("/")
   public static class UnlimitedResource
   {
      @Path("/unlimited2/{p:.*}")
      @GET
      public String hello()
      {
         return "hello world";
      }

      @Path(value = "/uriparam/{param:.*}")
      @GET
      public String get(@PathParam("param") String param, @QueryParam("expected") String expected)
      {
         System.out.println("expected: " + expected);
         Assert.assertEquals(param, expected);
         return "hello world";
      }
   }

   @Path("/")
   public static class LocatorResource
   {
      @Path(value = "/locator{p:.*}")
      public Object get()
      {
         return new Resource();
      }

      @Path(value = "/locator2/{param:.*}")
      public Object get(@PathParam("param") String param, @QueryParam("expected") String expected)
      {
         Assert.assertEquals(param, expected);
         return new Resource();
      }
   }

   @Path("/")
   public static class Locator3Resource
   {
      @Path("/locator3/unlimited")
      public Object get()
      {
         return new UnlimitedResource();
      }

      @Path("/locator3/uriparam/{param}")
      public Object uriParam(@PathParam("param") String param, @QueryParam("firstExpected") String expected)
      {
         Assert.assertEquals(param, expected);
         return new UnlimitedResource();
      }
   }

   public static class Resource
   {
      @GET
      public String hello()
      {
         return "hello world";
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(UnlimitedOnPathResource.class);
      dispatcher.getRegistry().addPerRequestResource(UnlimitedResource.class);
      dispatcher.getRegistry().addPerRequestResource(LocatorResource.class);
      dispatcher.getRegistry().addPerRequestResource(Locator3Resource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(HttpClient client, String path)
   {
      {
         GetMethod method = createGetMethod(path);
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

   @SuppressWarnings("unused")
   private void _testPut(HttpClient client, String path)
   {
      {
         PutMethod method = createPutMethod(path);
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
   public void testUnlimitedOnClass()
   {
      HttpClient client = new HttpClient();
      _test(client, "/unlimited");
      _test(client, "/unlimited/on/and/on");
   }

   @Test
   public void testUnlimitedOnMethod()
   {
      HttpClient client = new HttpClient();
      _test(client, "/unlimited2/on/and/on");
      _test(client, "/uriparam/on/and/on?expected=on%2Fand%2Fon");
   }

   @Test
   public void testLocator()
   {
      HttpClient client = new HttpClient();
      _test(client, "/locator");
      _test(client, "/locator/on/and/on");
      _test(client, "/locator2/on/and/on?expected=on%2Fand%2Fon");
      _test(client, "/locator3/unlimited/unlimited2/on/and/on");
      _test(client, "/locator3/unlimited/uriparam/on/and/on?expected=on%2Fand%2Fon");
      _test(client, "/locator3/uriparam/1/uriparam/on/and/on?firstExpected=1&expected=on%2Fand%2Fon");

   }
}