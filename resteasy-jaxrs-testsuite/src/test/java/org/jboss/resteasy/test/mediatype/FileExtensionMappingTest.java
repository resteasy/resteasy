package org.jboss.resteasy.test.mediatype;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.junit.Assert;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 3, 2012
 */
public class FileExtensionMappingTest
{
   protected ResteasyDeployment deployment;
   
   @Path("/test")
   static public class TestResource
   {
      @GET
      @Produces("text/plain")
      public String testPlain(@Context UriInfo uriInfo, @QueryParam("query") String query)
      {
         System.out.println(uriInfo.getRequestUri());
         return "plain: " + query;
      }
      
      @GET
      @Produces("text/html")
      public String testHtml(@Context UriInfo uriInfo, @QueryParam("query") String query)
      {
         System.out.println(uriInfo.getRequestUri());
         return "html: " + query;
      }
   }
   
   @Provider
   static public class TestApplication extends Application
   {
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }
   
   @Before
   public void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("javax.ws.rs.Application", TestApplication.class.getName());
      contextParams.put("resteasy.media.type.mappings", "txt : text/plain, html : text/html");
      deployment = EmbeddedContainer.start(initParams, contextParams);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }
   
   @Test
   public void testFileExtensionMappingPlain() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8081/test.txt?query=whosOnFirst").request().get();
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("plain: whosOnFirst", entity);
   }
   

   @Test
   public void testFileExtensionMappingHtml() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8081/test.html?query=whosOnFirst").request().get();
      System.out.println("status: " + response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("html: whosOnFirst", entity);
   }
}
