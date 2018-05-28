package org.jboss.resteasy.plugins.server.vertx;

import java.io.IOException;
import java.io.OutputStream;

import io.vertx.core.buffer.Buffer;
import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;

/**
 * Class to help application that are built to write to an
 * OutputStream to chunk the content
 * <pre>
 * {@code
 * DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
 * HttpHeaders.setTransferEncodingChunked(response);
 * response.headers().set(CONTENT_TYPE, "application/octet-stream");
 * //other headers
 * ctx.write(response);
 * // code of the application that use the ChunkOutputStream
 * // Don't forget to close the ChunkOutputStream after use!
 * ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
 * }
 * </pre>
 *
 * @author tbussier
 */
public class ChunkOutputStream extends OutputStream
{
   private Buffer buffer;
   private final VertxHttpResponse response;
   private final int chunkSize;

   ChunkOutputStream(VertxHttpResponse response, int chunksize)
   {
      this.response = response;
      if (chunksize < 1)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.chunkSizeMustBeAtLeastOne());
      }
      this.chunkSize = chunksize;
      this.buffer = Buffer.buffer(chunksize);
   }

   @Override
   public void write(int b) throws IOException
   {
      if (buffer.length() >= chunkSize - 1)
      {
         flush();
      }
      buffer.appendByte((byte) b);
   }

   public void reset()
   {
      if (response.isCommitted()) throw new IllegalStateException(Messages.MESSAGES.responseIsCommitted());
      buffer = Buffer.buffer(chunkSize);
   }

   @Override
   public void close() throws IOException
   {
      flush();
      super.close();
   }


   @Override
   public void write(byte[] b, int off, int len) throws IOException
   {
      int dataLengthLeftToWrite = len;
      int dataToWriteOffset = off;
      int spaceLeftInCurrentChunk;
      while ((spaceLeftInCurrentChunk = chunkSize - buffer.length()) < dataLengthLeftToWrite)
      {
         buffer.appendBytes(b, dataToWriteOffset, spaceLeftInCurrentChunk);
         dataToWriteOffset = dataToWriteOffset + spaceLeftInCurrentChunk;
         dataLengthLeftToWrite = dataLengthLeftToWrite - spaceLeftInCurrentChunk;
         flush();
      }
      if (dataLengthLeftToWrite > 0)
      {
         buffer.appendBytes(b, dataToWriteOffset, dataLengthLeftToWrite);
      }
   }

   @Override
   public void flush() throws IOException
   {
      int readable = buffer.length();
      if (readable == 0) return;
      if (!response.isCommitted()) response.prepareChunkStream();
      response.checkException();
      response.response.write(buffer);
      buffer = Buffer.buffer();
      super.flush();
   }

}