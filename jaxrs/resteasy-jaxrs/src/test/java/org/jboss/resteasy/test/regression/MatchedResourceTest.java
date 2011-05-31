package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.*;

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

      @Path("match")
      @Produces("*/*;q=0.0")
      @GET
      public String getObj()
      {
         return "*/*";
      }

      @Path("match")
      @Produces("application/xml")
      @GET
      public String getObjXml()
      {
         return "<xml/>";
      }

      @Path("match")
      @Produces("application/json")
      @GET
      public String getObjJson()
      {
         return "{ \"name\" : \"bill\" }";
      }

      @Path("start")
      @POST
      @Produces("text/plain")
      public String start()
      {
         return "started";
      }

      @Path("start")
      @Consumes("application/xml")
      @POST
      @Produces("text/plain")
      public String start(String xml)
      {
         return xml;
      }

   }

   /**
    * RESTEASY-549
    *
    * @throws Exception
    */
   @Test
   public void testEmpty() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/start"));
      String rtn = request.postTarget(String.class);
      Assert.assertEquals("started", rtn);

      request = new ClientRequest(generateURL("/start"));
      request.body("application/xml", "<xml/>");
      rtn = request.postTarget(String.class);
      Assert.assertEquals("<xml/>", rtn);

   }

   /**
    * RESTEASY-537
    *
    * @throws Exception
    */
   @Test
   public void testMatch() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/match"));
      request.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
      ClientResponse<String> rtn = request.get(String.class);
      Assert.assertEquals("text/html", rtn.getHeaders().getFirst("Content-Type"));
      String res = rtn.getEntity();
      Assert.assertEquals("*/*", res);
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