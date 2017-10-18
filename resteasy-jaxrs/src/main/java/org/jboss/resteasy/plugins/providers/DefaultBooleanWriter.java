package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 */
@SuppressWarnings("rawtypes")
@Provider
@Produces("text/plain")
public class DefaultBooleanWriter implements MessageBodyWriter<Boolean>
{

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return !String.class.equals(type) && !type.isArray();
   }

   @Override
   public void writeTo(Boolean t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
         WebApplicationException
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null)
      {
         charset = StandardCharsets.UTF_8.name();
      }
      try
      {
         entityStream.write(t.toString().getBytes(charset));
      }
      catch (UnsupportedEncodingException e)
      {
         // Use default encoding.
         entityStream.write(t.toString().getBytes());
      }
   }

   public long getSize(Boolean o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset != null)
         try
         {
            return o.toString().getBytes(charset).length;
         }
         catch (UnsupportedEncodingException e)
         {
            // Use default encoding.
         }
      return o.toString().getBytes(StandardCharsets.UTF_8).length;
   }

}
