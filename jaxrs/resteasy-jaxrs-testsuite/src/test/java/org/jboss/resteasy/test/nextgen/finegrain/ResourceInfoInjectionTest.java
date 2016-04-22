package org.jboss.resteasy.test.nextgen.finegrain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1281
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date April 20, 2016
 */
public class ResourceInfoInjectionTest
{
   protected static UndertowJaxrsServer server;
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static Client client;

   @Path("")
   public static class TestResource
   {
      @Context
      private HttpServletRequest request;

      @GET
      @Path("test")
      public String test()
      {
         return "abc";
      }

      @POST
      @Path("async")
      public void async(@Suspended final AsyncResponse async) throws IOException
      {
         final ServletInputStream inputStream = request.getInputStream();
         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

         inputStream.setReadListener(new ReadListener()
         {
            @Override
            public void onDataAvailable() throws IOException
            {
               IOUtils.copy(inputStream, outputStream);
            }

            @Override
            public void onAllDataRead() throws IOException
            {
               inputStream.close();
               outputStream.flush();
               outputStream.close();
               async.resume(outputStream.toString("UTF-8"));
            }

            @Override
            public void onError(Throwable t)
            {
               async.resume(t);
            }
         });
      }
   }

   @Provider
   public static class TestFilter implements ContainerResponseFilter
   {
      @Context
      private ResourceInfo resourceInfo;

      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         Method method = resourceInfo.getResourceMethod();
         System.out.println("method on response : " + method);
         if (method == null)
         {
            responseContext.setStatus(responseContext.getStatus() * 2);
         }
         else
         {
            responseContext.setEntity(method.getName(), null, MediaType.TEXT_PLAIN_TYPE);
         }
      }
   }

   @ApplicationPath("/app")
   public static class TestApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         Set<Class<?>> classes = new HashSet<>();
         classes.add(TestResource.class);
         classes.add(TestFilter.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
      server.deploy(TestApp.class);
      client = ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
      client.close();
   }

   @Test
   public void testNotFound() throws Exception
   {
      WebTarget target = client.target("http://localhost:8081/app/bogus");
      Response response = target.request().get();
      String entity = response.readEntity(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("response: " + entity);
      Assert.assertEquals(808, response.getStatus());
      Assert.assertEquals("", entity);
   }

   @Test
   public void testAsync() throws Exception
   {
      WebTarget target = client.target("http://localhost:8081/app/async");
      Response response = target.request().post(Entity.entity("hello", "text/plain"));
      String val = response.readEntity(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("response: " + val);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("async", val);
   }
}
