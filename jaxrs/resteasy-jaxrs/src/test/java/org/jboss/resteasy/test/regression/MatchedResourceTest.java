package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
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
public class MatchedResourceTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/test1/{id}.xml.{lang}")
      @GET
      public String getComplex()
      {
         return "complex";
      }

      @Path("/test1/{id}")
      @GET
      public String getSimple()
      {
         return "simple";
      }

      @Path("/test2/{id}")
      @GET
      public String getSimple2()
      {
         return "simple2";
      }

      @Path("/test2/{id}.xml.{lang}")
      @GET
      public String getComplex2()
      {
         return "complex2";
      }

   }

   public void _test(String uri, String value)
   {
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Assert.assertEquals(method.getResponseBodyAsString(), value);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         client.getHttpConnectionManager().closeIdleConnections(0);
      }
   }

   @Test
   public void testPost()
   {
      _test(generateURL("/test1/foo.xml.en"), "complex");
      _test(generateURL("/test2/foo.xml.en"), "complex2");
   }

}