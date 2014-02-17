package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WebApplicationExceptionTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class WebExceptionResource
   {
      @Path("/exception")
      @GET
      public Response get() throws WebApplicationException
      {
         throw new WebApplicationException(Response.status(HttpResponseCodes.SC_UNAUTHORIZED).build());

      }

      @Path("/exception/entity")
      @GET
      public Response getEntity() throws WebApplicationException
      {
         throw new WebApplicationException(Response.status(HttpResponseCodes.SC_UNAUTHORIZED).entity("error").build());

      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(WebExceptionResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(String path, int code)
   {
      ClientRequest request = new ClientRequest(generateURL(path));
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(code, response.getStatus());
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testException()
   {
      _test("/exception", HttpResponseCodes.SC_UNAUTHORIZED);
   }

   /**
    * Test JIRA bug RESTEASY-24
    */
   @Test
   public void testExceptionWithEntity()
   {
      _test("/exception/entity", HttpResponseCodes.SC_UNAUTHORIZED);
   }

}
