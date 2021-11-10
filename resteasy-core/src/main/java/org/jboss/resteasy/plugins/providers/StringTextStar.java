package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.NoContent;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class StringTextStar implements MessageBodyReader<String>, AsyncMessageBodyWriter<String>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return String.class.equals(type);
   }

   public String readFrom(Class<String> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException
   {
      if (NoContent.isContentLengthZero(httpHeaders)) return "";
      return ProviderHelper.readString(entityStream, mediaType);
   }


   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return String.class.equals(type) && !MediaTypeHelper.isBlacklisted(mediaType);
   }

   public long getSize(String o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(String o,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null) entityStream.write(o.getBytes(StandardCharsets.UTF_8));
      else entityStream.write(o.getBytes(charset));

   }

   @Override
   public CompletionStage<Void> asyncWriteTo(String o, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null)
         return entityStream.asyncWrite(o.getBytes(StandardCharsets.UTF_8));
      else {
         try
         {
            return entityStream.asyncWrite(o.getBytes(charset));
         } catch (UnsupportedEncodingException e)
         {
            return ProviderHelper.completedException(e);
         }
      }
   }
}
