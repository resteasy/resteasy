package org.jboss.resteasy.test.providers;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.*;

public class TestProvider extends BaseResourceTest
{

   private static final String TEST_URI = generateURL("/test");

   @Before
   public void setUp()
   {
      addPerRequestResource(DummyResource.class);
   }

   @Test
   public void testMessageReaderThrowingWebApplicationException() throws Exception
   {

      deployment.getProviderFactory().registerProviderInstance(new MessageBodyReader<DummyObject>()
      {

         public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediType)
         {
            return true;
         }

         public DummyObject readFrom(Class<DummyObject> type, Type genericType, Annotation[] annotations,
                                     MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                 throws IOException, WebApplicationException
         {
            throw new WebApplicationException(999); // deliberate crazy status
         }

      });

      ClientRequest request = new ClientRequest(TEST_URI);
      request.body("application/octet-stream", "foo");
      ClientResponse<?> response = request.post();
      Assert.assertEquals(999, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testMessageWriterThrowingWebApplicationException() throws Exception
   {

      deployment.getProviderFactory().registerProviderInstance(new MessageBodyWriter<DummyObject>()
      {
         public long getSize(DummyObject dummyObject, Class<?> type, Type genericType, Annotation[] annotations,
                             MediaType mediaType)
         {
            return -1;
         }

         public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
         {
            return true;
         }

         public void writeTo(DummyObject t, Class<?> type, Type genericType, Annotation[] annotations,
                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                 throws IOException, WebApplicationException
         {
            throw new WebApplicationException(999); // deliberate crazy status
         }

      });

      ClientRequest request = new ClientRequest(TEST_URI);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(999, response.getStatus());
      response.releaseConnection();
   }

}
