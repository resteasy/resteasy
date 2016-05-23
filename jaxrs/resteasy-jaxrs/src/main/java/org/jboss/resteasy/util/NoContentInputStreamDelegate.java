package org.jboss.resteasy.util;

import javax.ws.rs.core.NoContentException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NoContentInputStreamDelegate extends InputStream
{
   protected InputStream delegate;
   protected boolean hasRead;

   public NoContentInputStreamDelegate(InputStream delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public int read() throws IOException
   {
      int amount = delegate.read();
      return check(amount);
   }

   private int check(int amount) throws NoContentException
   {
      if (amount > -1) hasRead = true;
      else if (!hasRead)
      {
         throw new NoContentException(Messages.MESSAGES.noContent());
      }
      return amount;
   }

   @Override
   public int read(byte[] b) throws IOException
   {
      int amount = delegate.read(b);
      return check(amount);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException
   {
      int amount = delegate.read(b, off, len);
      return check(amount);
   }

   @Override
   public long skip(long n) throws IOException
   {
      return delegate.skip(n);
   }

   @Override
   public int available() throws IOException
   {
      return delegate.available();
   }

   @Override
   public void close() throws IOException
   {
      delegate.close();
   }

   @Override
   public void mark(int readlimit)
   {
      delegate.mark(readlimit);
   }

   @Override
   public void reset() throws IOException
   {
      delegate.reset();
   }

   @Override
   public boolean markSupported()
   {
      return delegate.markSupported();
   }
}
