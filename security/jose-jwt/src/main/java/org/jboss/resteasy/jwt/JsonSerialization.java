package org.jboss.resteasy.jwt;

import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonSerialization
{
   public static byte[] toByteArray(Object token, boolean indent) throws Exception
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      factory.register(new JWTContextResolver(indent));
      factory.register(ResteasyJacksonProvider.class);

      return toByteArray(token, factory);

   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public static byte[] toByteArray(Object token, ResteasyProviderFactory factory) throws IOException
   {
      MessageBodyWriter writer = factory.getMessageBodyWriter(token.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE);
      if (writer == null) throw new NullPointerException(Messages.MESSAGES.couldNotFindMessageBodyWriterForJSON());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      Providers old = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, factory);
      try
      {
         writer.writeTo(token, token.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE, new MultivaluedHashMap<String, Object>(), baos);

         return baos.toByteArray();
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (old != null) ResteasyProviderFactory.pushContext(Providers.class, old);
      }
   }

   public static String toString(Object token, ResteasyProviderFactory factory) throws Exception
   {
      byte[] bytes = toByteArray(token, factory);
      return new String(bytes);
   }


   public static String toString(Object token, boolean indent) throws Exception
   {
      byte[] bytes = toByteArray(token, indent);
      return new String(bytes);
   }

   public static <T> T fromString(Class<T> type, String json) throws Exception
   {
      byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
      return fromBytes(type, bytes);

   }

   public static <T> T fromString(Class<T> type, String json, ResteasyProviderFactory factory) throws Exception
   {
      byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
      return fromBytes(type, bytes, factory);

   }


   public static <T> T fromBytes(Class<T> type, byte[] bytes) throws IOException
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      factory.register(ResteasyJacksonProvider.class);
      factory.register(JWTContextResolver.class);

      return fromBytes(type, bytes, factory);
   }

   public static <T> T fromBytes(Class<T> type, byte[] bytes, ResteasyProviderFactory factory) throws IOException
   {
      MessageBodyReader<T> reader = factory.getMessageBodyReader(type, type, null, MediaType.APPLICATION_JSON_TYPE);
      if (reader == null) throw new NullPointerException(Messages.MESSAGES.couldNotFindMessageBodyReaderForJSON());
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

      Providers old = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, factory);
      try
      {
         return reader.readFrom(type, type, null, MediaType.APPLICATION_JSON_TYPE, new MultivaluedHashMap<String, String>(), bais);
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (old != null) ResteasyProviderFactory.pushContext(Providers.class, old);
      }
   }
}
