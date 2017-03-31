package org.jboss.resteasy.test;

import io.netty.channel.ChannelHandlerContext;

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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;

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

      @PUT
      @Path("/leak")
      public String put(String contents) {
         return contents;
      }

      @GET
      @Path("/test/absolute")
      @Produces("text/plain")
      public String absolute(@Context UriInfo info)
      {
         return "uri: " + info.getRequestUri().toString();
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
   public void testHeadContentLength() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/test"));
      Response getResponse = target.request().buildGet().invoke();
      String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
      Assert.assertEquals("hello world", val);
      Assert.assertEquals("chunked", getResponse.getHeaderString("transfer-encoding"));
      Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
      Assert.assertNull(headResponse.getHeaderString("Content-Length"));
      Assert.assertNull(headResponse.getHeaderString("transfer-encoding"));
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

   @Test
   public void testLeak() {
      // Run test with -Dio.netty.leakDetection.level=paranoid -Dio.netty.leakDetection.maxRecords=10000
      WebTarget target = client.target(generateURL("/leak"));
      for (int i = 0; i < 1000; i++) {
         String putBody = "some data #" + i;
         String result = target.request().put(Entity.text(putBody), String.class);
         Assert.assertEquals(putBody, result);
      }
   }


   /**
    * Per the HTTP spec, we must allow requests like:
    *
    * <pre>
    *     GET http://www.example.com/content HTTP/1.1
    *     Host: www.example.com
    * </pre>
    *
    * <blockquote>
    * RFC 2616 5.1.12:
    * To allow for transition to absoluteURIs in all requests in future
    versions of HTTP, all HTTP/1.1 servers MUST accept the absoluteURI
    form in requests, even though HTTP/1.1 clients will only generate
    them in requests to proxies.
    </blockquote>
    * @throws Exception
    */
   @Test
   public void testAbsoluteURI() throws Exception {
      String uri = generateURL("/test/absolute");

      Socket client = new Socket(getHost(), getPort());
      PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out.printf(Locale.US, "GET %s HTTP/1.1\nHost: %s:%d\n\n", uri, getHost(), getPort());
      String statusLine = in.readLine();
      String response = in.readLine();
      while (!response.startsWith("uri"))
      {
         response = in.readLine();
      }
      client.close();
      Assert.assertEquals("HTTP/1.1 200 OK", statusLine);
      Assert.assertEquals(uri, response.subSequence(5, response.length()));
   }
}
