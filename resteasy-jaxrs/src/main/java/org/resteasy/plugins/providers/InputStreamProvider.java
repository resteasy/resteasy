package org.resteasy.plugins.providers;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ProduceMime("*/*")
@ConsumeMime("*/*")
public class InputStreamProvider implements MessageBodyReader<InputStream>, MessageBodyWriter<InputStream>
{
   public boolean isReadable(Class<?> type)
   {
      return type.equals(InputStream.class);
   }

   public InputStream readFrom(Class<InputStream> type, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      return entityStream;
   }

   public boolean isWriteable(Class<?> type)
   {
      return type.equals(InputStream.class);
   }

   public long getSize(InputStream inputStream)
   {
      return -1;
   }

   public void writeTo(InputStream inputStream, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      int c;
      while ((c = inputStream.read()) != -1)
      {
         entityStream.write(c);
      }
   }

}
