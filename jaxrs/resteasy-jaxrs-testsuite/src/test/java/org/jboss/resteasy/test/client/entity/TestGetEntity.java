package org.jboss.resteasy.test.client.entity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Unit tests for RESTEASY-1251.
 * 
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date January 16, 2016
 */
public class TestGetEntity
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static final String ANSWER = "answer";

   @Path("/")
   public static class TestResource
   {
     @GET
     @Path("test")
     public String test()
     {
    	 return ANSWER;
     }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }


   @Test
   public void testGetEntity()
   {
	   ResteasyClient client = new ResteasyClientBuilder().build();
       WebTarget base = client.target("http://localhost:8081/test");
       Response response = base.request().get();
       Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
       Assert.assertEquals(ANSWER, response.readEntity(String.class));
       Assert.assertTrue(response.hasEntity());
       Assert.assertEquals(ANSWER, response.getEntity());
       response.close();
       try
       {
    	   response.readEntity(String.class);
    	   Assert.fail("Expected Exception");
       }
       catch (IllegalStateException e)
       {
    	   // Good
       }
       catch (Exception e)
       {
    	   Assert.fail("Expected IllegalStateException");
       }
       try
       {
    	   response.getEntity();
    	   Assert.fail("Expected Exception");
       }
       catch (IllegalStateException e)
       {
    	   // Good
       }
       catch (Exception e)
       {
    	   Assert.fail("Expected IllegalStateException");
       }
       try
       {
    	   response.hasEntity();
    	   Assert.fail("Expected Exception");
       }
       catch (IllegalStateException e)
       {
    	   // Good
       }
       catch (Exception e)
       {
    	   Assert.fail("Expected IllegalStateException");
       }
       client.close();
   }
}
