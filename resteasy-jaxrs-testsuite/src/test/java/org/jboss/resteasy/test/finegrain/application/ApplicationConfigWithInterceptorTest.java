package org.jboss.resteasy.test.finegrain.application;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationConfigWithInterceptorTest
{
   private static Client client;
   
   @Path("/my")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      @Path("/good")
      public String get()
      {
         return "hello";
      }

      @GET
      @Produces("text/plain")
      @Path("/bad")
      public String response()
      {
         throw new WebApplicationException(Response.status(Status.CONFLICT).entity("conflicted").build());
      }

      @DELETE
      @Path("{id}")
      public void remove(@PathParam("id") String id)
      {
         return;
      }
   }

   @Provider
   public static class AddHeader implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println("HERE!!!!!");
         responseContext.getHeaders().add("custom-header", "hello");
      }
   }

   public static class MyApplicationConfig extends Application
   {
      private Set<Class<?>> classes = new HashSet<Class<?>>();

      public MyApplicationConfig()
      {
         classes.add(MyResource.class);
         classes.add(AddHeader.class);
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return classes;
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplication(new MyApplicationConfig());
      EmbeddedContainer.start(deployment);
      client = ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Test
   public void testNormalReturn() throws Exception
   {
      doTest("/my/good", 200);
   }

   @Test
   public void testWebApplicationExceptionWithResponse() throws Exception
   {
      doTest("/my/bad", 409);
   }

   @Test
   public void testNoContentResponse() throws Exception
   {
      doTest("/my/123", 204, false);
   }

   private void doTest(String path, int expectedStatus) throws Exception
   {
      doTest(path, expectedStatus, true);
   }

   private void doTest(String path, int expectedStatus, boolean get) throws Exception
   {
      Builder builder = client.target(generateURL(path)).request();
      Response response = get ? builder.get() : builder.delete();
      Assert.assertEquals(expectedStatus, response.getStatus());
      Assert.assertNotNull(response.getHeaderString("custom-header"));
      response.close();
   }
}
