package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("text/plain")
public class DefaultNumberWriter implements MessageBodyWriter<Number>
{
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // StringTextStar should pick up strings
      return !String.class.equals(type) && !type.isArray();
   }

   @Override
   public long getSize(Number n, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return convertToBytes(n, mediaType).length;
   }

   @Override
   public void writeTo(Number n, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      byte[] bytes = convertToBytes(n, mediaType);
      entityStream.write(bytes);
   }
   
   private byte[] convertToBytes(Number n, MediaType mediaType)
   {
      String charset = mediaType.getParameters().get("charset");
      if (charset == null)
      {
         charset = StandardCharsets.UTF_8.name();
      }
      
      if (n instanceof Byte)
      {
         try
         {
            return Byte.toString(n.byteValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            // Use default encoding.
            return Byte.toString(n.byteValue()).getBytes();
         }
      }
      
      if (n instanceof Double)
      {
         try
         {
            return Double.toString(n.doubleValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return Double.toString(n.doubleValue()).getBytes();
         }
      }
      
      if (n instanceof Float)
      {
         try
         {
            return Float.toString(n.floatValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return Float.toString(n.floatValue()).getBytes();
         }
      }
      
      if (n instanceof Integer)
      {
         try
         {
            return Integer.toString(n.intValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return Integer.toString(n.intValue()).getBytes();
         }
      }
      
      if (n instanceof Long)
      {
         try
         {
            return Long.toString(n.longValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return Long.toString(n.longValue()).getBytes();
         }
      }
      
      if (n instanceof Short)
      {
         try
         {
            return Short.toString(n.shortValue()).getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return Short.toString(n.shortValue()).getBytes();
         }
      }
      
      if (n instanceof BigDecimal)
      {
         try
         {
            return BigDecimal.class.cast(n).toString().getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return BigDecimal.class.cast(n).toString().getBytes();
         }
      }

      if (n instanceof BigInteger)
      {
         try
         {
            return BigInteger.class.cast(n).toString().getBytes(charset);
         }
         catch (UnsupportedEncodingException e)
         {
            return BigInteger.class.cast(n).toString().getBytes();
         }
      }
      
      throw new RuntimeException(Messages.MESSAGES.unexpectedNumberSubclass(n.getClass().getName()));
   }
}
