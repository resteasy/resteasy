package org.jboss.resteasy.test.providers;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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

public class TestProvider extends BaseResourceTest
{


   private static final String TEST_URI = "http://localhost:8081/test";


   @Before
   public void setUp()
   {
      addPerRequestResource(DummyResource.class);
   }


   @Test
   public void testMessageReaderThrowingWebApplicationException() throws Exception
   {

      dispatcher.getProviderFactory().registerProviderInstance(new
              MessageBodyReader<DummyObject>()
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

              }
      );

      HttpClient client = new HttpClient();

      PostMethod method = new PostMethod(TEST_URI);
      method.setRequestEntity(new StringRequestEntity("foo", "application/octet-stream", "utf-8"));
      int status = client.executeMethod(method);
      Assert.assertEquals(999, status);
   }


   @Test
   public void testMessageWriterThrowingWebApplicationException() throws Exception
   {

      dispatcher.getProviderFactory().registerProviderInstance(new MessageBodyWriter<DummyObject>()
      {
         public long getSize(DummyObject dummyObject, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
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

      HttpClient client = new HttpClient();

      GetMethod method = new GetMethod(TEST_URI);
      int status = client.executeMethod(method);
      Assert.assertEquals(999, status);
   }

}
