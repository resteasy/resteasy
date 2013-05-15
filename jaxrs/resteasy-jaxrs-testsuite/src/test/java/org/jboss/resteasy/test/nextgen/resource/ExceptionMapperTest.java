package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperTest extends BaseResourceTest
{
   @Provider
   public static class RuntimeExceptionMapper implements
           ExceptionMapper<RuntimeException>
   {

      @Override
      public Response toResponse(RuntimeException exception) {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }
   }

   @Provider
   public static class WebAppExceptionMapper implements
           ExceptionMapper<WebApplicationException> {

      @Override
      public Response toResponse(WebApplicationException exception) {
         // When not found, i.e. url is wrong, one get also
         // WebApplicationException
         if (exception.getClass() != WebApplicationException.class)
            return exception.getResponse();
         return Response.status(Response.Status.ACCEPTED).build();
      }

   }

   @Path("resource")
   public static class Resource {
      @GET
      @Path("responseok")
      public String responseOk() {
         Response r = Response.ok("hello").build();
         throw new WebApplicationException(r);
      }

   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(WebAppExceptionMapper.class);
      deployment.getProviderFactory().register(RuntimeExceptionMapper.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testWAEResponseUsed()
   {
      Response response = client.target(generateURL("/resource/responseok")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
   }


}
