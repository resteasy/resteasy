package org.jboss.resteasy.test.nextgen.resource;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

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
   public static class ThrowableMapper implements
           ExceptionMapper<Throwable>
   {

      @Override
      public Response toResponse(Throwable exception) {
         return Response.ok(getClass().getName()).build();
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
   public static class MyCustomExceptionMapper extends AbstractExceptionMapper<MyCustomException> {
      @Override
      protected void handleError(final Response.ResponseBuilder builder, final MyCustomException e) {
         builder.entity("custom").type(MediaType.TEXT_HTML_TYPE);
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

      @GET
      @Path("throwable")
      public String throwable() throws Throwable
      {
         throw new Throwable(new RuntimeException(new ClientErrorException(499)));
      }

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
      deployment.getProviderFactory().register(ThrowableMapper.class);
      deployment.getProviderFactory().register(WebAppExceptionMapper.class);
      deployment.getProviderFactory().register(RuntimeExceptionMapper.class);
      deployment.getProviderFactory().register(MyCustomExceptionMapper.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   //@Test
   public void testThrowable()
   {
      Response response = client.target(generateURL("/resource/throwable")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(ThrowableMapper.class.getName(), response.readEntity(String.class));
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



   @Test
   public void testWAEResponseUsed()
   {
      Response response = client.target(generateURL("/resource/responseok")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals("hello", response.readEntity(String.class));
   }


}
