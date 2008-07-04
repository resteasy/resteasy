package org.jboss.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EncodedPathTest
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
      @Path("/hello world")
      @GET
      public String get()
      {
         System.out.println("Hello");
         return "HELLO";
      }

      @Path(value = "/goodbye%7Bworld", encode = false)
      @GET
      public String goodbye()
      {
         System.out.println("Goodbye");
         return "GOODBYE";
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
   public void testEncoded() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test(new HttpClient(), "http://localhost:8081/hello%20world");
         _test(new HttpClient(), "http://localhost:8081/goodbye%7Bworld");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
}