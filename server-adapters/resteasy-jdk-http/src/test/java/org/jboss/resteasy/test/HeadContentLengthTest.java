package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

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
      httpServer.stop(0);
   }

   @Test
   public void testBasic() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/test"));
      String val = target.request().get(String.class);
      Assert.assertEquals("hello world", val);
   }

   @Test
   public void testHeadContentLength() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/test"));
      Response getResponse = target.request().buildGet().invoke();
      String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
      Assert.assertEquals("hello world", val);
      Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
      Assert.assertNull(headResponse.getHeaderString("Content-Length"));
   }
}
