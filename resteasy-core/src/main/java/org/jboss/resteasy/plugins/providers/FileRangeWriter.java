package org.jboss.resteasy.plugins.providers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;

import com.ibm.asyncutil.iteration.AsyncTrampoline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class FileRangeWriter implements AsyncMessageBodyWriter<FileRange>
{
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(FileRange.class) && !MediaTypeHelper.isBlacklisted(mediaType);
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

   @Override
   public CompletionStage<Void> asyncWriteTo(FileRange fileRange, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream)
   {
      long fileSize = fileRange.getFile().length();
      String contentRange = "bytes " + fileRange.getBegin() + "-" + fileRange.getEnd() + "/" + fileSize;
      long length = (fileRange.getEnd() - fileRange.getBegin()) + 1;
      httpHeaders.putSingle("Content-Range", contentRange);
      httpHeaders.putSingle("Content-Length", length);
      try
      {
         FileInputStream fis = new FileInputStream(fileRange.getFile());
         if (fileRange.getBegin() > 0)
         {
            fis.getChannel().position(fileRange.getBegin());
         }
         final byte[] buf = new byte[2048];
         return writeTo(fis, length, entityStream, buf)
               .whenComplete((v, t) -> {
                  try
                  {
                     fis.close();
                  } catch (IOException e)
                  {
                     throw new RuntimeException(e);
                  }
               });
      } catch (IOException e)
      {
         return ProviderHelper.completedException(e);
      }
   }

   private CompletionStage<Void> writeTo(FileInputStream fis, long length, AsyncOutputStream entityStream, byte[] buf)
   {
      if(length > 0) {
         long[] mutableLength = new long[] {length};
         return AsyncTrampoline.asyncWhile(
               read -> read != -1,
               read -> entityStream.asyncWrite(buf, 0, read)
               .thenApply(v -> {
                  mutableLength[0] -= read;
                  if(mutableLength[0] > 0)
                     return ProviderHelper.asyncRead(fis, buf, 0, (int)Math.min(buf.length, mutableLength[0]));
                  // simulate EOF to exit the loop
                  return -1;
               }),
               ProviderHelper.asyncRead(fis, buf, 0, (int)Math.min(buf.length, mutableLength[0]))
               ).thenApply(v -> null);
      }
      return CompletableFuture.completedFuture(null);
   }
}
