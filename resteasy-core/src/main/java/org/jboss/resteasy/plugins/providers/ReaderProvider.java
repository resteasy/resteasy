package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">BillBurke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
// FIXME: this does not implement AsyncMessageBodyWriter, but I think the current implementation is flawed as we read chars and write
// them as if they were bytes, which will never work outside of ASCII
public class ReaderProvider implements MessageBodyReader<Reader>, MessageBodyWriter<Reader>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(Reader.class);
   }

   public Reader readFrom(Class<Reader> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      String charset = mediaType.getParameters().get("charset");
      if (charset == null) return new InputStreamReader(entityStream);
      else return new InputStreamReader(entityStream, charset);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Reader.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType);
   }

   public long getSize(Reader inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Reader inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      try
      {
         int c = inputStream.read();
         if (c == -1)
         {
            httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(0));
            entityStream.write(new byte[0]); // fix RESTEASY-204
            return;
         }
         else
            entityStream.write(c);
         while ((c = inputStream.read()) != -1)
         {
            entityStream.write(c);
         }
      }
      finally
      {
         try
         {
            inputStream.close();
         }
         catch (IOException e)
         {
            // Drop the exception so we don't mask real IO errors
         }
      }
   }
}
