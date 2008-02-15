package org.resteasy.plugins.providers;

import org.resteasy.util.StringToPrimitive;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ProduceMime("text/plain")
@ConsumeMime("text/plain")
public class DefaultTextPlain implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
   public boolean isReadable(Class<?> type)
   {
      if (type.isPrimitive()) return true;
      if (String.class.equals(type)) return true;
      try
      {
         type.getConstructor(String.class);
         return true;
      }
      catch (NoSuchMethodException ignored)
      {

      }
      try
      {
         type.getDeclaredMethod("valueOf", String.class);
         return true;
      }
      catch (NoSuchMethodException e)
      {
      }
      return false;
   }

   public Object readFrom(Class<Object> type, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      char[] buffer = new char[100];
      StringBuffer buf = new StringBuffer();
      BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));

      int wasRead = 0;
      do
      {
         wasRead = reader.read(buffer, 0, 100);
         if (wasRead > 0) buf.append(buffer, 0, wasRead);
      } while (wasRead > -1);

      String val = buf.toString();

      if (type.isPrimitive()) return StringToPrimitive.stringToPrimitiveBoxType(type, val);
      if (type.equals(String.class)) return val;

      try
      {
         return type.getConstructor(String.class).newInstance(val);
      }
      catch (NoSuchMethodException ignored)
      {
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getCause());
      }
      try
      {
         return type.getMethod("valueOf", String.class).invoke(null, val);
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException("Could not unmarshal from plain text");
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getCause());
      }
   }

   public boolean isWriteable(Class<?> type)
   {
      return true;
   }

   public long getSize(Object o)
   {
      return o.toString().getBytes().length;
   }

   public void writeTo(Object o, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      entityStream.write(o.toString().getBytes());
   }
}
