package org.jboss.resteasy.test;

import junit.framework.Assert;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContractsTest
{
   public static class Data {}

   @Produces(MediaType.APPLICATION_ATOM_XML)
   @Consumes(MediaType.APPLICATION_ATOM_XML)
   public static class DataReaderWriter implements MessageBodyReader<Data>, MessageBodyWriter<Data>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public Data readFrom(Class<Data> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         return null;
      }

      @Override
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public long getSize(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return 0;
      }

      @Override
      public void writeTo(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {

      }
   }

   @Test
   public void testLimitedContract()
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      factory.register(DataReaderWriter.class, MessageBodyReader.class);
      MessageBodyReader reader = factory.getMessageBodyReader(Data.class, Data.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
      Assert.assertNotNull(reader);
      MessageBodyWriter writer = factory.getMessageBodyWriter(Data.class, Data.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
      Assert.assertNull(writer);
   }

   @Test
   public void testLimitedContractMap()
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      Map<Class<?>, Integer> contract = new HashMap<Class<?>, Integer>();
      contract.put(MessageBodyReader.class, 5);
      factory.register(DataReaderWriter.class, contract);
      MessageBodyReader reader = factory.getMessageBodyReader(Data.class, Data.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
      Assert.assertNotNull(reader);
      MessageBodyWriter writer = factory.getMessageBodyWriter(Data.class, Data.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
      Assert.assertNull(writer);
   }

}
