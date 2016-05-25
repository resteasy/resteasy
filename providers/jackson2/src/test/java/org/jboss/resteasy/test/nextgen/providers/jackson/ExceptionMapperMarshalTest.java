package org.jboss.resteasy.test.nextgen.providers.jackson;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.Types;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperMarshalTest extends BaseResourceTest
{
   public static class Name
   {
      String first;

      public Name()
      {
      }

      public Name(String first)
      {
         this.first = first;
      }

      public String getFirst()
      {
         return first;
      }

      public void setFirst(String first)
      {
         this.first = first;
      }
   }

   public static class ErrorMessage
   {
      String error;

      public ErrorMessage(String error)
      {
         this.error = error;
      }

      public ErrorMessage()
      {
      }

      public String getError()
      {
         return error;
      }

      public void setError(String error)
      {
         this.error = error;
      }
   }

   public static class MyCustomException extends RuntimeException {
      public MyCustomException(final String message) {
         super(message);
      }
   }

   @Provider
   public static class MyCustomExceptionMapper implements ExceptionMapper<MyCustomException> {
      @Override
      public Response toResponse(MyCustomException exception)
      {
         List<ErrorMessage> list = new ArrayList<ErrorMessage>();
         list.add(new ErrorMessage("error"));
         return Response.ok(list, MediaType.APPLICATION_JSON_TYPE).build();
      }
   }

   @Path("resource")
   public static class Resource {
      @GET
      @Path("custom")
      public List<Name> custom() throws Throwable
      {
         throw new MyCustomException("hello");
      }


   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(MyCustomExceptionMapper.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   /**
    * RESTEASY-937
    *
    */
   @Test
   public void testCustomUsed()
   {
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(MyCustomExceptionMapper.class, ExceptionMapper.class)[0];
      Assert.assertEquals(MyCustomException.class, exceptionType);

      Response response = client.target(generateURL("/resource/custom")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      List<ErrorMessage> errors = response.readEntity(new GenericType<List<ErrorMessage>>(){});
      Assert.assertEquals("error", errors.get(0).getError());
   }
}
