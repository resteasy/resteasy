package org.jboss.resteasy.test.nextgen.finegrain.resource;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created December 6, 2017
 */
public class Step2ResourceMatchingTest
{
	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;

   @ApplicationPath("")
   public static class TestApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @Path("")
   public static class TestResource
   { 
      @GET
      @Path("/test/abc")
      public Response m1()
      {
         return Response.ok("m1()").build();
      }

      @POST
      @Path("/test/{path}")
      public Response m2(@PathParam("path") String path, String s) {
         return Response.ok("m2()").build();
      }
   }

   //////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void before() throws Exception
	{
		deployment = EmbeddedContainer.start();
        ((ResourceMethodRegistry)deployment.getRegistry()).setWiderMatching(true);
		dispatcher = deployment.getDispatcher();
		deployment.getRegistry().addPerRequestResource(TestResource.class);
	}

	@AfterClass
	public static void after() throws Exception
	{
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
		Thread.sleep(100);
	}

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testResourceMethodChoice() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8081/test/abc").request();
      Response response = request.post(Entity.entity("xyz", MediaType.TEXT_PLAIN));
      String s = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("m2()", s);
      client.close();
   }
}