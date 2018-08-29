package org.jboss.resteasy.test.finegrain.application;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
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
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationConfigWithInterceptorTest
{

   private static final Logger LOG = Logger.getLogger(ApplicationConfigWithInterceptorTest.class);

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
   public static class AddHeader implements PostProcessInterceptor
   {
      public void postProcess(ServerResponse response)
      {
         LOG.info("HERE!!!!!");
         response.getMetadata().add("custom-header", "hello");
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
   }

   @AfterClass
   public static void after() throws Exception
   {
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

   @SuppressWarnings("unchecked")
   private void doTest(String path, int expectedStatus) throws Exception
   {
      doTest(path, expectedStatus, true);
   }

   @SuppressWarnings("unchecked")
   private void doTest(String path, int expectedStatus, boolean get) throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL(path));
      ClientResponse response = get ? request.get() : request.delete();
      Assert.assertEquals(expectedStatus, response.getStatus());
      Assert.assertNotNull(response.getResponseHeaders().getFirst("custom-header"));

   }
}
