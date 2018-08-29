package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.plugins.server.servlet.Cleanable;
import org.jboss.resteasy.plugins.server.servlet.Cleanables;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.NoContent;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:mlittle@redhat.com">Mark Little</a>
 * @version $Revision: 1 $
 */

@Provider
@Produces("*/*")
@Consumes("*/*")
public class FileProvider implements MessageBodyReader<File>,
        MessageBodyWriter<File>
{
   private static final String PREFIX = "pfx";

   private static final String SUFFIX = "sfx";

   private String _downloadDirectory = null; // by default temp dir, but
   // consider allowing it to be
   // defined at runtime

   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType)
   {
      return File.class == type;
   }

   public File readFrom(Class<File> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
           throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      File downloadedFile = null;

      if (_downloadDirectory != null)
      {
         try
         {
            downloadedFile = File.createTempFile(PREFIX, SUFFIX, new File(
                    _downloadDirectory));
         }
         catch (final IOException ex)
         {
            // could make this configurable, so we fail on fault rather than
            // default.
            LogMessages.LOGGER.couldNotBindToDirectory(_downloadDirectory);
         }
      }

      if (downloadedFile == null)
         downloadedFile = File.createTempFile(PREFIX, SUFFIX);

      Cleanables cleanables = ResteasyProviderFactory.getContextData(Cleanables.class);
      if (cleanables != null)
      {
         cleanables.addCleanable(new FileHolder(downloadedFile));
      }
      else
      {
         LogMessages.LOGGER.temporaryFileCreated(downloadedFile.getPath());
      }
      
      if (NoContent.isContentLengthZero(httpHeaders)) return downloadedFile;
      OutputStream output = new BufferedOutputStream(new FileOutputStream(
              downloadedFile));

      try
      {
         ProviderHelper.writeTo(entityStream, output);
      }
      finally
      {
         output.close();
      }

      return downloadedFile;
   }

   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType)
   {
      return File.class.isAssignableFrom(type); // catch subtypes
   }

   public long getSize(File o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return o.length();
   }

   public void writeTo(File uploadFile, Class<?> type, Type genericType,
                       Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      HttpHeaders headers = ResteasyProviderFactory.getContextData(HttpHeaders.class);
      if (headers == null)
      {
         writeIt(uploadFile, entityStream);
         return;
      }
      String range = headers.getRequestHeaders().getFirst("Range");
      if (range == null)
      {
         writeIt(uploadFile, entityStream);
         return;
      }
      range = range.trim();
      int byteUnit = range.indexOf("bytes=");
      if ( byteUnit < 0)
      {
    	  //must start with 'bytes'
          writeIt(uploadFile, entityStream);
          return;
      }
      range = range.substring("bytes=".length());
      if (range.indexOf(',') > -1)
      {
         // we don't support this
         writeIt(uploadFile, entityStream);
         return;
      }
      int separator = range.indexOf('-');
      if (separator < 0)
      {
         writeIt(uploadFile, entityStream);
         return;
      }
      else if (separator == 0)
      {
         long fileSize = uploadFile.length();
         long begin = Long.parseLong(range);
         if (fileSize + begin < 1)
         {
            writeIt(uploadFile, entityStream);
            return;
         }
         throw new FileRangeException(mediaType, uploadFile, fileSize + begin, fileSize - 1);
      }
      else
      {
         try
         {
            long fileSize = uploadFile.length();
            long begin = Long.parseLong(range.substring(0, separator));
            if (begin >= fileSize)
            {
               throw new WebApplicationException(416);
            }
            long end;
            if (range.endsWith("-"))
            {
               end = fileSize - 1;
            }
            else
            {
               String substring = range.substring(separator + 1);
               end = Long.parseLong(substring);
            }
            /*
            if (begin == 0 && end + 1 >= fileSize)
            {
               writeIt(uploadFile, entityStream);
               return;
            }
            */
            throw new FileRangeException(mediaType, uploadFile, begin, end);
         }
         catch (NumberFormatException e)
         {
            writeIt(uploadFile, entityStream);
            return;
         }
      }
   }

   protected void writeIt(File uploadFile, OutputStream entityStream) throws IOException
   {
      InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFile));

      try
      {
         ProviderHelper.writeTo(inputStream, entityStream);
      }
      finally
      {
         inputStream.close();
      }
   }
   
   private static class FileHolder implements Cleanable
   {
      File file;
      
      FileHolder(File file)
      {
         this.file = file;
      }

      @Override
      public void clean() throws Exception
      {
         file.delete();
      }
   }
}
