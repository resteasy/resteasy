package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.NoContent;
import org.jboss.resteasy.util.TypeConverter;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
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
@SuppressWarnings({"unchecked", "rawtypes"})
@Provider
@Produces("text/plain")
@Consumes("text/plain")
public class DefaultTextPlain implements MessageBodyReader, AsyncMessageBodyWriter
{
   public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // StringTextStar should pick up strings
      return !String.class.equals(type) && TypeConverter.isConvertable(type);
   }

   public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      InputStream delegate = NoContent.noContentCheck(httpHeaders, entityStream);
      String value = ProviderHelper.readString(delegate, mediaType);
      return TypeConverter.getType(type, value);
   }

   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // StringTextStar should pick up strings
      return !String.class.equals(type) && !type.isArray();
   }

   public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset != null)
         try
         {
            return o.toString().getBytes(charset).length;
         } catch (UnsupportedEncodingException e)
         {
            // Use default encoding.
         }
      return o.toString().getBytes(StandardCharsets.UTF_8).length;
   }

   public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null) entityStream.write(o.toString().getBytes(StandardCharsets.UTF_8));
      else entityStream.write(o.toString().getBytes(charset));
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(Object o, Class type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap httpHeaders, AsyncOutputStream entityStream)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null)
         return entityStream.asyncWrite(o.toString().getBytes(StandardCharsets.UTF_8));
      else {
         try
         {
            return entityStream.asyncWrite(o.toString().getBytes(charset));
         } catch (UnsupportedEncodingException e)
         {
            return ProviderHelper.completedException(e);
         }
      }
   }
}
