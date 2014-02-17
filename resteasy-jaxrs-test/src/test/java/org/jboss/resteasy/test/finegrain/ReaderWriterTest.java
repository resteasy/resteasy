package org.jboss.resteasy.test.finegrain;

import junit.framework.Assert;
import org.jboss.resteasy.core.messagebody.ReaderUtility;
import org.jboss.resteasy.core.messagebody.WriterUtility;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Test for ReaderUtility and WriterUtility
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class ReaderWriterTest
{

   public static class TestObject
   {
      public String value;

      public TestObject(String value)
      {
         this.value = value;
      }

      public TestObject(byte[] buff)
      {
         this.value = new String(buff);
      }
   }

   @Produces("application/test-content")
   @Consumes("application/test-content")
   public static class MyProvider implements MessageBodyReader<TestObject>,
           MessageBodyWriter<TestObject>
   {

      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(TestObject.class);
      }

      public TestObject readFrom(Class<TestObject> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
              throws IOException, WebApplicationException
      {
         byte[] result = ReadFromStream.readFromStream(1024, entityStream);
         return new TestObject(new String(result).replaceAll("^BeginTest\\{",
                 "").replaceAll("\\}EndTest$", ""));
      }

      public long getSize(TestObject t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(TestObject.class);
      }

      public void writeTo(TestObject t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException
      {
         new PrintStream(entityStream).append("BeginTest{").append(t.value)
                 .append("}EndTest");
      }

   }

   @BeforeClass
   public static void setup()
   {
      ResteasyProviderFactory provider = ResteasyProviderFactory.getInstance();
      RegisterBuiltin.register(provider);
      provider.registerProvider(MyProvider.class);
   }

   @Test
   public void readObject() throws IOException
   {
      Assert.assertEquals("test", ReaderUtility.read(String.class, "text/lain", "test"));
      Assert.assertEquals("test", ReaderUtility.read(TestObject.class,
              "application/test-content", "BeginTest{test}EndTest").value);
   }

   @Test
   public void writeObject() throws IOException
   {
      Assert.assertEquals("test", new String(WriterUtility.getBytes("test",
              "text/plain")));

      Assert.assertEquals("BeginTest{test}EndTest", WriterUtility.asString(
              new TestObject("test"), "application/test-content"));
   }
}
