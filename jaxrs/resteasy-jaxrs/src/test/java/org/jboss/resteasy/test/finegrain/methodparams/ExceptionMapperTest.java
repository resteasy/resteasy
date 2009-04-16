package org.jboss.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;

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
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(Throwme.class);
      ResteasyProviderFactory.getInstance().addExceptionMapper(MyExceptionMapper.class);
      ResteasyProviderFactory.getInstance().addExceptionMapper(NotFoundMapper.class);
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
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/providers");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testMapping()
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NOT_MODIFIED);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testSubclassMapping()
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/subclass");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NOT_MODIFIED);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testResteasyExceptionMapping()
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/notexist");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 410);
         Assert.assertTrue(notFoundMapper);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();

   }
}
