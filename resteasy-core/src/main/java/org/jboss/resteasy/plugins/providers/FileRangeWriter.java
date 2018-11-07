package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class FileRangeWriter implements MessageBodyWriter<FileRange>
{
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(FileRange.class);
   }

   @Override
   public long getSize(FileRange fileRange, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(FileRange fileRange, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      long fileSize = fileRange.getFile().length();
      String contentRange = "bytes " + fileRange.getBegin() + "-" + fileRange.getEnd() + "/" + fileSize;
      long length = (fileRange.getEnd() - fileRange.getBegin()) + 1;
      httpHeaders.putSingle("Content-Range", contentRange);
      httpHeaders.putSingle("Content-Length", length);
      FileInputStream fis = new FileInputStream(fileRange.getFile());
      try
      {
         if (fileRange.getBegin() > 0)
         {
            fis.getChannel().position(fileRange.getBegin());
         }
         final byte[] buf = new byte[2048];
         while (length > 0)
         {
            int len = 2048 > length ? (int)length : 2048;
            int read = fis.read(buf, 0, len);
            if (read == -1)
            {
               break;
            }
            entityStream.write(buf, 0, read);
            length -= len;
         }
      }
      finally
      {
         fis.close();
      }

   }
}
