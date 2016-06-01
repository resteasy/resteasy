package org.jboss.resteasy.test.interceptors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * RESTEASY-433
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreProcessorExceptionMapperTest extends BaseResourceTest
{
   public static class CandlepinException extends RuntimeException
   {
   }

   public static class CandlepinUnauthorizedException extends CandlepinException
   {
   }


   @Provider
   public static class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>
   {
      public Response toResponse(RuntimeException exception)
      {
         return Response.status(412).build();
      }
   }

   @Provider
   public static class PreProcessSecurityInterceptor implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure, WebApplicationException
      {
         throw new CandlepinUnauthorizedException();
      }
   }

   @Path("/interception")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }
   }

   @Before
   public void setUp() throws Exception
   {
      deployment.getProviderFactory().registerProvider(PreProcessSecurityInterceptor.class);
      deployment.getProviderFactory().registerProvider(RuntimeExceptionMapper.class);
      addPerRequestResource(MyResource.class);
   }

   @Test
   public void testMapper() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/interception"));
      ClientResponse res = request.get();
      Assert.assertEquals(412, res.getStatus());

   }

}
