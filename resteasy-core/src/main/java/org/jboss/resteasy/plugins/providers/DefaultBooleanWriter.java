package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;

/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 */
@SuppressWarnings("rawtypes")
@Provider
@Produces("text/plain")
public class DefaultBooleanWriter implements AsyncMessageBodyWriter<Boolean>
{

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return !String.class.equals(type) && !type.isArray() && !MediaTypeHelper.isBlacklisted(mediaType);
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

   @Override
   public CompletionStage<Void> asyncWriteTo(Boolean t, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null)
      {
         charset = StandardCharsets.UTF_8.name();
      }
      try
      {
         return entityStream.asyncWrite(t.toString().getBytes(charset));
      }
      catch (UnsupportedEncodingException e)
      {
         // Use default encoding.
         return entityStream.asyncWrite(t.toString().getBytes());
      }
   }

}
