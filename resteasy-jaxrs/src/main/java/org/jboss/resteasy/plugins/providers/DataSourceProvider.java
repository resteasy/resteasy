/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.plugins.server.servlet.Cleanable;
import org.jboss.resteasy.plugins.server.servlet.Cleanables;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.NoContent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Consumes("*/*")
@Produces("*/*")
public class DataSourceProvider extends AbstractEntityProvider<DataSource>
{

   protected static class SequencedDataSource implements DataSource
   {
      private final byte[] byteBuffer;
      private final int byteBufferOffset;
      private final int byteBufferLength;
      private final File tempFile;
      private final String type;

      protected SequencedDataSource(byte[] byteBuffer, int byteBufferOffset,
                                 int byteBufferLength, File tempFile, String type)
      {
         super();
         this.byteBuffer = byteBuffer;
         this.byteBufferOffset = byteBufferOffset;
         this.byteBufferLength = byteBufferLength;
         this.tempFile = tempFile;
         this.type = type;
      }

      @Override
      public String getContentType()
      {
         return type;
      }

      @Override
      public InputStream getInputStream() throws IOException
      {
         InputStream bis = new ByteArrayInputStream(byteBuffer, byteBufferOffset, byteBufferLength);
         if (tempFile == null)
            return bis;
         InputStream fis = new FileInputStream(tempFile);
         return new SequenceInputStream(bis, fis);
      }

      @Override
      public String getName()
      {
         return "";
      }

      @Override
      public OutputStream getOutputStream() throws IOException
      {
         throw new IOException(Messages.MESSAGES.noOutputStreamAllowed());
      }

   }


   /**
    * @param in
    * @param mediaType
    * @return
    * @throws IOException
    */
   public static DataSource readDataSource(final InputStream in, final MediaType mediaType) throws IOException
   {
      byte[] memoryBuffer = new byte[4096];
      int readCount = in.read(memoryBuffer, 0, memoryBuffer.length);

      File tempFile = null;
      if (readCount > 0)
      {
         byte[] buffer = new byte[4096];
         int count = in.read(buffer, 0, buffer.length);
         if (count > -1) {
             tempFile = File.createTempFile("resteasy-provider-datasource", null);
             FileOutputStream fos = new FileOutputStream(tempFile);
             Cleanables cleanables = ResteasyProviderFactory.getContextData(Cleanables.class);
             if (cleanables != null)
             {
                cleanables.addCleanable(new TempFileCleanable(tempFile));
             }
             fos.write(buffer, 0, count);
             try
             {
                ProviderHelper.writeTo(in, fos);
             }
             finally
             {
                fos.close();
             }
         }
      }

      if (readCount == -1)
         readCount = 0;

      return new SequencedDataSource(memoryBuffer, 0, readCount, tempFile, mediaType.toString());
   }

   /**
    * FIXME Comment this
    *
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see javax.ws.rs.ext.MessageBodyReader
    */
   @Override
   public boolean isReadable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations, MediaType mediaType)
   {
      return DataSource.class.isAssignableFrom(type);
   }


   /**
    * FIXME Comment this
    *
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @return
    * @throws IOException
    * @throws WebApplicationException
    * @see @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
    */
   @Override
   public DataSource readFrom(Class<DataSource> type,
                              Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType,
                              MultivaluedMap<String, String> httpHeaders,
                              InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      if (NoContent.isContentLengthZero(httpHeaders)) return readDataSource(new ByteArrayInputStream(new byte[0]), mediaType);
      return readDataSource(entityStream, mediaType);
   }


   /**
    * FIXME Comment this
    *
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[])
    */
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return DataSource.class.isAssignableFrom(type);
   }

   /**
    * FIXME Comment this
    *
    * @param dataSource
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @throws IOException
    * @throws WebApplicationException
    * @see @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
    */
   @Override
   public void writeTo(DataSource dataSource,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      InputStream in = dataSource.getInputStream();
      try
      {
         ProviderHelper.writeTo(in, entityStream);
      }
      finally
      {
         in.close();
      }

   }

   private static class TempFileCleanable implements Cleanable {

      private File tempFile;

      public TempFileCleanable(File tempFile) {
         this.tempFile = tempFile;
      }

      @Override
      public void clean() throws Exception {
          if(tempFile.exists())
          {
             if (!tempFile.delete()) //set delete on exit only if the file can't be deleted now
             {
                tempFile.deleteOnExit();
             }
          }
      }
   }
}
