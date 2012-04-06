package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
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

            System.err
                    .println("Could not bind to specified download directory "
                            + _downloadDirectory + " so will use temp dir.");
         }
      }

      if (downloadedFile == null)
         downloadedFile = File.createTempFile(PREFIX, SUFFIX);

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
}
