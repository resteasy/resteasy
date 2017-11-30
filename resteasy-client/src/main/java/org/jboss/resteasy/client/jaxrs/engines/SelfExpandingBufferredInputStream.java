package org.jboss.resteasy.client.jaxrs.engines;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * Extension of {@link BufferedInputStream} enforcing the contract where reset()
 * always returns to the beginning of the stream, and the internal buffer
 * expands automatically to the total length of content read from the underlying
 * stream.
 *
 * @author ul8b
 */
public class SelfExpandingBufferredInputStream extends BufferedInputStream
{
   private static int defaultBufferSize = 8192;

   public SelfExpandingBufferredInputStream(InputStream in)
   {
      super(in);
      super.mark(defaultBufferSize);
   }

   public SelfExpandingBufferredInputStream(InputStream in, int size)
   {
      super(in, size);
      super.mark(size);
   }

   /**
    * Not supported. Mark position is always zero.
    */
   @Override
   public synchronized void mark(int readlimit)
   {
      throw new UnsupportedOperationException(Messages.MESSAGES.alwaysMarkedAtIndex0());
   }

   @Override
   public synchronized int read() throws IOException
   {
      if (pos == marklimit)
      {
         expand();
      }
      return super.read();
   }

   @Override
   public synchronized int read(byte[] b, int off, int len) throws IOException
   {
      while (pos + len > marklimit)
      {
         expand();
      }
      return super.read(b, off, len);
   }

   @Override
   public synchronized int read(byte[] b) throws IOException
   {
      while (pos + b.length > marklimit)
      {
         expand();
      }
      return super.read(b);
   }

   /**
    * Double the current buffer size limit. Reset to zero, then double the
    * buffer size and restore last position in the buffer.
    *
    * @throws IOException
    */
   private void expand() throws IOException
   {
      int lastPos = pos;
      super.reset();
      super.mark((marklimit < (Integer.MAX_VALUE - 8) / 2) ? (marklimit * 2) : (Integer.MAX_VALUE - 8));
      pos = lastPos;
   }

   /**
    * Return the current maximum size of the internal buffer. This is
    * independent of how much data is actually contained within the buffer.
    */
   public int getBufSize()
   {
      return buf.length;
   }

   public int getCount()
   {
      return count;
   }

   public int getPos()
   {
      return pos;
   }

   public int getMarkLimit()
   {
      return marklimit;
   }

   public int getMarkPos()
   {
      return markpos;
   }
}