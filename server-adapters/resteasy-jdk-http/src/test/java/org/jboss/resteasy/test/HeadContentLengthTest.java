package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.net.InetSocketAddress;

public class HeadContentLengthTest
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
   }

   private static HttpServer httpServer;
   private static HttpContextBuilder contextBuilder;

   @BeforeClass
   public static void before() throws Exception
   {
      int port = TestPortProvider.getPort();
      httpServer = HttpServer.create(new InetSocketAddress(port), 10);
      contextBuilder = new HttpContextBuilder();
      contextBuilder.getDeployment().getActualResourceClasses().add(Resource.class);
      contextBuilder.bind(httpServer);
      httpServer.start();

   }

   @AfterClass
   public static void after() throws Exception
   {
      contextBuilder.cleanup();
      httpServer.stop(1);
   }

   @Test
   public void testBasic() throws Exception
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/test"));
      String val = target.request().get(String.class);
      Assert.assertEquals("hello world", val);
   }

   @Test
   public void testHeadContentLength() throws Exception
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/test"));
      Response getResponse = target.request().buildGet().invoke();
      String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
      Assert.assertEquals("hello world", val);
      Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
      Assert.assertNull(headResponse.getHeaderString("Content-Length"));
   }
}
