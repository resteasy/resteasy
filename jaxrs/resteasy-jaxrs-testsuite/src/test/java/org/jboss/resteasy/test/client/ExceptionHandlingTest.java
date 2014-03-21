package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class ExceptionHandlingTest extends BaseResourceTest
{
   @Path("/")
   public static class ThrowsExceptionResource
   {
      @Path("test")
      @POST
      public void post() throws Exception { throw new Exception("test"); }
   }

   @Path("/")
   public interface ThrowsExceptionInterface
   {
      @Path("test")
      @POST
      public void post() throws Exception;
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(ThrowsExceptionResource.class);
   }

   @Test
   public void testThrowsException() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();

      ThrowsExceptionInterface proxy = client.target(generateURL("/")).proxy(ThrowsExceptionInterface.class);
      try {
          proxy.post();
      } catch (InternalServerErrorException e) {
          Response response = e.getResponse();
          String errorText = response.readEntity(String.class);
          Assert.assertNotNull(errorText);
      }

      client.close();
   }

}
