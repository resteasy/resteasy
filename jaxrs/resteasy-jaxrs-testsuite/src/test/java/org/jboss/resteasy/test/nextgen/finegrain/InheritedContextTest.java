package org.jboss.resteasy.test.nextgen.finegrain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-952.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 22, 2013
 */
public class InheritedContextTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("super")
   static public class BaseService
   {  
      @Context
      protected UriInfo uriInfo;
      
      @Context
      protected HttpHeaders httpHeaders;
      
      @Context
      protected Request request;
      
      @Context
      protected SecurityContext securityContext;
      
      @Context
      protected Providers providers;
      
      @Context
      protected ResourceContext resourceContext;
      
      @Context
      protected Configuration configuration;
      
      @Path("test/{level}")
      @GET
      public String test(@PathParam("level") String level)
      {
         System.out.println("BaseService.test()");
         return Boolean.toString(level.equals("BaseService") && testContexts());
      }
      
      protected boolean testContexts()
      {
         return uriInfo != null
               && httpHeaders != null
               && request != null
               && securityContext != null
               && providers != null
               && resourceContext != null
               && configuration != null;
      }
   }

   @Path("sub")
   static public class SomeService extends BaseService
   {
      @Path("test/{level}")
      @GET
      public String test(@PathParam("level") String level)
      {
         System.out.println("SomeService.test()");
         return Boolean.toString(level.equals("SomeService") && testContexts());
      }
   }
   
   @Path("subsub")
   static public class SomeSubService extends SomeService
   {
      @Path("test/{level}")
      @GET
      public String test(@PathParam("level") String level)
      {
         System.out.println("SomeSubService.test()");
         return Boolean.toString(level.equals("SomeSubService") && testContexts());
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(BaseService.class);
      deployment.getRegistry().addPerRequestResource(SomeService.class);
      deployment.getRegistry().addPerRequestResource(SomeSubService.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testContext() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target(TestPortProvider.generateURL("/super/test/BaseService")).request();
      Response response = request.get();
      System.out.println("status: " + response.getStatus());
      String s = response.readEntity(String.class);
      System.out.println("response: " + s);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }

   @Test
   public void testInheritedContextOneLevel() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target(TestPortProvider.generateURL("/sub/test/SomeService")).request();
      Response response = request.get();
      System.out.println("status: " + response.getStatus());
      String s = response.readEntity(String.class);
      System.out.println("response: " + s);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }
   
   @Test
   public void testInheritedContextTwoLevels() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target(TestPortProvider.generateURL("/subsub/test/SomeSubService")).request();
      Response response = request.get();
      System.out.println("status: " + response.getStatus());
      String s = response.readEntity(String.class);
      System.out.println("response: " + s);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }
}
