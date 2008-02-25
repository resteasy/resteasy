package org.resteasy.plugins.providers;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ProduceMime("*/*")
@ConsumeMime("*/*")
public class ByteArrayProvider implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]>
{
   public boolean isReadable(Class<?> type)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public byte[] readFrom(Class<byte[]> type, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));

      byte[] buffer = new byte[100];
      int wasRead = 0;
      do
      {
         wasRead = entityStream.read(buffer, 0, 100);
         if (wasRead > 0)
         {
            baos.write(buffer, 0, wasRead);
         }
      } while (wasRead > -1);
      return baos.toByteArray();
   }

   public boolean isWriteable(Class<?> type)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public long getSize(byte[] bytes)
   {
      return bytes.length;
   }

   public void writeTo(byte[] bytes, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      entityStream.write(bytes);
   }
}
