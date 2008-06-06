package org.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class Throwme
   {
      @GET
      public String get() throws JAXBException
      {
         throw new JAXBException("FAILURE!!!");
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(Throwme.class);
      ResteasyProviderFactory.getInstance().addExceptionMapper(JAXBMapper.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Provider
   public static class JAXBMapper implements ExceptionMapper<JAXBException>
   {
      public Response toResponse(JAXBException exception)
      {
         return Response.notModified().build();
      }
   }

   @Test
   public void testRegisteredCorrectly()
   {
      Assert.assertNotNull(ResteasyProviderFactory.getInstance().createExceptionMapper(JAXBException.class));
   }

   @Test
   public void testMapping()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081");
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
}
