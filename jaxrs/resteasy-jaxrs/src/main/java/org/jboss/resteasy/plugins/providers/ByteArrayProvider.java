package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.util.NoContent;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.plugins.i18n.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Produces("*/*")
@Consumes("*/*")
public class ByteArrayProvider implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]>
{
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.ByteArrayProvider , method call : isReadable .")
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.ByteArrayProvider , method call : readFrom .")
   public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      if (NoContent.isContentLengthZero(httpHeaders)) return new byte[0];
      return ReadFromStream.readFromStream(1024, entityStream);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.ByteArrayProvider , method call : isWriteable .")
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.ByteArrayProvider , method call : getSize .")
   public long getSize(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return bytes.length;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.ByteArrayProvider , method call : writeTo .")
   public void writeTo(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      entityStream.write(bytes);
   }
}
