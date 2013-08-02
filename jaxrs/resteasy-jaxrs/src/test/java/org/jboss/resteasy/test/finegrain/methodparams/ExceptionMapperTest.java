package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperTest
{
   private static Dispatcher dispatcher;

   public static class MyException extends Exception
   {
      public MyException()
      {
      }

      public MyException(String s)
      {
         super(s);
      }

      public MyException(String s, Throwable throwable)
      {
         super(s, throwable);
      }

      public MyException(Throwable throwable)
      {
         super(throwable);
      }
   }

   public static class SubclassException extends MyException
   {
      public SubclassException(String s)
      {
         super(s);
      }
   }

   @Path("/")
   public static class Throwme
   {
      @GET
      public String get() throws MyException
      {
         throw new MyException("FAILURE!!!");
      }

      @Path("subclass")
      @GET
      public String getSubclass() throws MyException
      {
         throw new SubclassException("FAILURE!!!");
      }

      @Path("providers")
      @GET
      public String getProvidersTest(@Context Providers providers)
      {
         Assert.assertNotNull(providers);
         Assert.assertEquals(providers.getExceptionMapper(MyException.class).getClass(), MyExceptionMapper.class);
         return "stuff";
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Throwme.class);
      ResteasyProviderFactory.getInstance().registerProvider(MyExceptionMapper.class);
      ResteasyProviderFactory.getInstance().registerProvider(NotFoundMapper.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Provider
   public static class MyExceptionMapper implements ExceptionMapper<MyException>
   {
      public Response toResponse(MyException exception)
      {
         return Response.notModified().build();
      }
   }

   @Test
   public void testRegisteredCorrectly()
   {
      Assert.assertNotNull(ResteasyProviderFactory.getInstance().getExceptionMapper(MyException.class));
      Assert.assertNotNull(ResteasyProviderFactory.getInstance().getExceptionMapper(NotFoundException.class));
   }

   private static boolean notFoundMapper = false;

   @Provider
   public static class NotFoundMapper implements ExceptionMapper<NotFoundException>
   {
      public Response toResponse(NotFoundException exception)
      {
         notFoundMapper = true;
         return Response.status(410).build();
      }
   }

   @Test
   public void testProvidersInjection()
   {
      ClientRequest request = new ClientRequest(generateURL("/providers"));
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testMapping()
   {
      ClientRequest request = new ClientRequest(generateURL(""));
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testSubclassMapping()
   {
      ClientRequest request = new ClientRequest(generateURL("/subclass"));
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testResteasyExceptionMapping()
   {
      ClientRequest request = new ClientRequest(generateURL("/notexist"));
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(410, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
