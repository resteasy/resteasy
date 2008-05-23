package org.resteasy.test.finegrain;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.specimpl.MessageBodyWorkersImpl;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWorkers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProviderInjectionTest
{
   @BeforeClass
   public static void before()
   {
      ResteasyProviderFactory.setInstance(new ResteasyProviderFactory());
   }

   @ConsumeMime("not/real")
   public static class MyProviderReader implements MessageBodyReader
   {
      @Context
      public HttpHeaders headers;

      @Context
      public MessageBodyWorkers workers;

      public boolean isReadable(Class type, Type genericType, Annotation[] annotations)
      {
         return false;
      }

      public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         return null;
      }
   }

   @Test
   public void testCacheControl()
   {
      MyProviderReader reader = new MyProviderReader();
      ResteasyProviderFactory.getInstance().addMessageBodyReader(reader);

      Assert.assertNotNull(reader.headers);
      Assert.assertNotNull(reader.workers);
      Assert.assertEquals(reader.workers.getClass(), MessageBodyWorkersImpl.class);
   }

}