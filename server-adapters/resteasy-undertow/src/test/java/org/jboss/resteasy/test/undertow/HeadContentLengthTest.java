package org.jboss.resteasy.test.undertow;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class HeadContentLengthTest
{
   private static UndertowJaxrsServer server;

   @Path("/test")
   public static class Resource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }
   }

   @ApplicationPath("/base")
   public static class MyApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(Resource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
   }

   @Test
   public void testHeadContentLength() throws Exception
   {
      server.deploy(MyApp.class);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/base/test"));
      Response getResponse = target.request().buildGet().invoke();
      String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
      Assert.assertEquals("hello world", val);
      Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
      Assert.assertEquals("HEAD method should return the same Content-Length as the GET method", getResponse.getLength(), headResponse.getLength());
      Assert.assertTrue(getResponse.getLength() > 0);
      client.close();
   }

}
