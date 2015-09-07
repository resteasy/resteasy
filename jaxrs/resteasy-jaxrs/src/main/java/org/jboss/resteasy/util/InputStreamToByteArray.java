package org.jboss.resteasy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InputStreamToByteArray extends InputStream
{
   protected InputStream delegate;
   protected ByteArrayOutputStream os = new ByteArrayOutputStream();

   public InputStreamToByteArray(InputStream delegate)
   {
      this.delegate = delegate;
   }

   public byte[] toByteArray()
   {
      return os.toByteArray();
   }

   @Override
   public int read()
           throws IOException
   {
      int b = delegate.read();
      if (b > -1)
         os.write((byte) b);
      return b;
   }

   @Override
   public int read(byte[] bytes)
           throws IOException
   {
      int num = delegate.read(bytes);
      if (num > 0)
      {
         os.write(bytes, 0, num);
      }
      return num;
   }

   @Override
   public int read(byte[] bytes, int off, int len)
           throws IOException
   {
      int num = delegate.read(bytes, off, len);
      if (num > 0)
      {
         os.write(bytes, off, num);
      }
      return num;
   }

   @Override
   public long skip(long l)
           throws IOException
   {
      return delegate.skip(l);
   }

   @Override
   public int available()
           throws IOException
   {
      return delegate.available();
   }

   @Override
   public void close()
           throws IOException
   {
      delegate.close();
   }

   @Override
   public void mark(int i)
   {
      delegate.mark(i);
   }

   @Override
   public void reset()
           throws IOException
   {
      throw new RuntimeException(Messages.MESSAGES.streamWrappedBySignature());
   }

   @Override
   public boolean markSupported()
   {
      return delegate.markSupported();
   }
}
