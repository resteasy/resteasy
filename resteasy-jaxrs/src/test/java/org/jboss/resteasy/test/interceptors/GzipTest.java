package org.jboss.resteasy.test.interceptors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GzipTest extends BaseResourceTest
{

   @Path("/")
   public static interface IGZIP
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public String getText();

      @GET
      @Path("encoded/text")
      @GZIP
      public String getGzipText();
   }

   @Path("/")
   public static class GZIPService
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public Response getText(@Context HttpHeaders headers)
      {
         String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
         System.out.println(acceptEncoding);
         Assert.assertEquals("gzip, deflate", acceptEncoding);
         return Response.ok("HELLO WORLD").header("Content-Encoding", "gzip").build();
      }

      @GET
      @Path("encoded/text")
      @GZIP
      public String getGzipText()
      {
         return "HELLO WORLD";
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(GZIPService.class);
   }


   @Test
   public void testProxy() throws Exception
   {
      IGZIP proxy = ProxyFactory.create(IGZIP.class, generateBaseUrl());
      Assert.assertEquals("HELLO WORLD", proxy.getText());
      Assert.assertEquals("HELLO WORLD", proxy.getGzipText());
   }

   @Test
   public void testRequest() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/text"));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("HELLO WORLD", response.getEntity());

   }

   @Test
   public void testRequest2() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/encoded/text"));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("HELLO WORLD", response.getEntity());

   }

   @Test
   public void testWasZipped() throws Exception
   {
      // test that it was zipped by running it through Apache HTTP Client which does not automatically unzip

      HttpClient client = new HttpClient();
      {
         GetMethod get = new GetMethod(TestPortProvider.generateURL("/encoded/text"));
         get.addRequestHeader("Accept-Encoding", "gzip, deflate");
         int status = client.executeMethod(get);
         Assert.assertEquals(200, status);
         String response = get.getResponseBodyAsString();
         Assert.assertEquals("gzip", get.getResponseHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         Assert.assertNotSame(response, "HELLO WORLD");
      }


      {
         GetMethod get = new GetMethod(TestPortProvider.generateURL("/text"));
         get.addRequestHeader("Accept-Encoding", "gzip, deflate");
         int status = client.executeMethod(get);
         Assert.assertEquals(200, status);
         String response = get.getResponseBodyAsString();
         Assert.assertEquals("gzip", get.getResponseHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         Assert.assertNotSame(response, "HELLO WORLD");
      }
   }

   @Test
   public void testWithoutAcceptEncoding() throws Exception
   {
      // test that if there is no accept-encoding: gzip header that result isn't encoded

      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod(TestPortProvider.generateURL("/encoded/text"));
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      String response = get.getResponseBodyAsString();
      Assert.assertNull(get.getResponseHeader("Content-Encoding"));

      // test that it is actually zipped
      Assert.assertEquals(response, "HELLO WORLD");

   }


}
