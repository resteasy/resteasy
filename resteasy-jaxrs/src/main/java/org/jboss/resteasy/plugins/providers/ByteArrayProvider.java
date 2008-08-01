package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.util.ReadFromStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Produces("*/*")
@Consumes("*/*")
public class ByteArrayProvider implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      return ReadFromStream.readFromStream(1024, entityStream);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public long getSize(byte[] bytes)
   {
      return bytes.length;
   }

   public void writeTo(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      entityStream.write(bytes);
   }
}
