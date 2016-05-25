package org.jboss.resteasy.test.nextgen.client;

import static org.junit.Assert.fail;
import io.undertow.servlet.api.DeploymentInfo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-981
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright April 16, 2015
 */
public class ExceptionBufferingTest
{  
   private static UndertowJaxrsServer server;
   protected static ResteasyClient client;
   
   @ApplicationPath("")
   public static class TestApplication extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }
   
   @Path("/")
   static public class TestResource
   {
      @GET
      @Path("test")
      public String test() 
      {
         System.out.println("entering test()");
         Response response = Response.serverError().entity("test").build();
         throw new WebApplicationException(response);
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
      DeploymentInfo diBuffer = server.undertowDeployment(TestApplication.class);
      diBuffer.addInitParameter("resteasy.buffer.exception.entity", "true");
      diBuffer.setDeploymentName("buffer");
      diBuffer.setContextPath("/buffer");
      server.deploy(diBuffer);
      DeploymentInfo diNoBuffer = server.undertowDeployment(TestApplication.class);
      diNoBuffer.addInitParameter("resteasy.buffer.exception.entity", "false");
      diNoBuffer.setDeploymentName("nobuffer");
      diNoBuffer.setContextPath("/nobuffer");
      server.deploy(diNoBuffer);
      DeploymentInfo diDefault = server.undertowDeployment(TestApplication.class);
      diDefault.setDeploymentName("default");
      diDefault.setContextPath("/default");
      server.deploy(diDefault);
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
   }
   
   @Test
   public void testBufferedResponseDefault() throws Exception
   {
      Response response = null;
      
      try
      {
         ResteasyWebTarget target = client.target("http://localhost:8081/default/test");
         Invocation invocation = target.request().buildGet();
         response = invocation.invoke();
         System.out.println("status: " + response.getStatus());
         String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
         fail("Was expecting an exception: " + s);
      }
      catch (Exception e)
      {
         System.out.println("caught: " + e);
         String entity = response.readEntity(String.class);
         System.out.println("exception entity: " + entity);
         Assert.assertEquals("test", entity);
      }
   }
   
   @Test
   public void testBufferedResponseFalse() throws Exception
   {
      Response response = null;
      
      try
      {
         ResteasyWebTarget target = client.target("http://localhost:8081/nobuffer/test");
         Invocation invocation = target.request().buildGet();
         response = invocation.invoke();
         System.out.println("status: " + response.getStatus());
         String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
         fail("Was expecting an exception: " + s);
      }
      catch (Exception e)
      {
         System.out.println("caught: " + e);
         try
         {
            String s = response.readEntity(String.class);
            fail("Was expecting a second exception: " + s);
         }
         catch (ProcessingException e1)
         {
            System.out.println("and caught: " + e1);
            Assert.assertTrue(e1.getCause() instanceof IOException);
            Assert.assertEquals("Attempted read on closed stream.", e1.getCause().getMessage());
         }
         catch (Exception e1)
         {
            fail("Was expecting a ProcessingException instead of " + e1);
         }
      }
   }
   
   @Test
   public void testBufferedResponseTrue() throws Exception
   {
      Response response = null;
      
      try
      {
         ResteasyWebTarget target = client.target("http://localhost:8081/buffer/test");
         Invocation invocation = target.request().buildGet();
         response = invocation.invoke();
         System.out.println("status: " + response.getStatus());
         String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
         fail("Was expecting an exception: " + s);
      }
      catch (Exception e)
      {
         System.out.println("caught: " + e);
         String entity = response.readEntity(String.class);
         System.out.println("exception entity: " + entity);
         Assert.assertEquals("test", entity);
      }
   }
}
