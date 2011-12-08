package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * RESTEASY-595
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperRuntimeException3Test extends BaseResourceTest
{
   public static class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
   {
      public Response toResponse(WebApplicationException exception)
      {
         return Response.status(Response.Status.PRECONDITION_FAILED).header("custom", "header").build();
      }
   }

   public static class FailureExceptionMapper implements ExceptionMapper<Failure>
   {
      @Override
      public Response toResponse(Failure exception)
      {
         return Response.status(Response.Status.FORBIDDEN).header("custom", "header").build();
      }
   }

   @Path("/test")
   public static class MyService
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         throw new WebApplicationException(401);
      }

      @GET
      @Path("failure")
      @Produces("text/plain")
      public String getFailure()
      {
         return "hello";
      }
   }

   @Before
   public void init() throws Exception
   {
      getProviderFactory().addExceptionMapper(new FailureExceptionMapper());
      getProviderFactory().addExceptionMapper(new WebApplicationExceptionMapper());
      addPerRequestResource(MyService.class);
   }


   @Test
   public void testWebAPplicationException() throws Exception
   {
      ClientRequest request = new ClientRequest(generateBaseUrl() + "/test");
      ClientResponse response = request.get();
      Assert.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
      Assert.assertEquals(response.getResponseHeaders().getFirst("custom"), "header");
   }

   @Test
   public void testFailure() throws Exception
   {
      ClientRequest request = new ClientRequest(generateBaseUrl() + "/test/failure");
      request.accept("application/xml");
      ClientResponse response = request.get();
      Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
      Assert.assertEquals(response.getResponseHeaders().getFirst("custom"), "header");
   }


}