package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.Types;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * RESTEASY-666
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapper3Test extends BaseResourceTest
{
   public static class MyCustomException extends RuntimeException {
      public MyCustomException(final String message) {
         super(message);
      }
   }

   public static abstract class AbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

      @Override
      public Response toResponse(final E exception) {
         final Response.ResponseBuilder builder = Response.ok();

         handleError(builder, exception);

         return builder.build();
      }

      protected abstract void handleError(final Response.ResponseBuilder builder, E e);
   }

   @Provider
   public static class DefaultExceptionMapper extends AbstractExceptionMapper<RuntimeException> {
      @Override
      protected void handleError(final Response.ResponseBuilder builder, final RuntimeException e) {
         builder.entity("default").type(MediaType.TEXT_HTML_TYPE);
      }
   }

   @Provider
   public static class MyCustomExceptionMapper extends AbstractExceptionMapper<MyCustomException> {
      @Override
      protected void handleError(final Response.ResponseBuilder builder, final MyCustomException e) {
         builder.entity("custom").type(MediaType.TEXT_HTML_TYPE);
      }
   }

   @Path("resource")
   public static class Resource {
    @GET
      @Path("custom")
      public String custom() throws Throwable
      {
         throw new MyCustomException("hello");
      }


   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(DefaultExceptionMapper.class);
      deployment.getProviderFactory().register(MyCustomExceptionMapper.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testCustomUsed()
   {
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(MyCustomExceptionMapper.class, ExceptionMapper.class)[0];
      Assert.assertEquals(MyCustomException.class, exceptionType);

      Response response = client.target(generateURL("/resource/custom")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals("custom", response.readEntity(String.class));
   }
}
