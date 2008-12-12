package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.util.TypeConverter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("text/plain")
@Consumes("text/plain")
public class DefaultTextPlain implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return TypeConverter.isConvertable(type);
   }

   public Object readFrom(Class<Object> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException
   {
      String value = ProviderHelper.readString(entityStream, mediaType);
      return TypeConverter.getType(type, value);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return !type.isArray();
   }

   public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return o.toString().getBytes().length;
   }

   public void writeTo(Object o,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      entityStream.write(o.toString().getBytes());
   }
}
