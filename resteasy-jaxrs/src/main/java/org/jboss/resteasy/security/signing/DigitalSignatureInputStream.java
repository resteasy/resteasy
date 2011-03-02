package org.jboss.resteasy.security.signing;

import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DigitalSignatureInputStream extends InputStream
{
   protected InputStream delegate;
   protected Signature signature;

   public DigitalSignatureInputStream(InputStream delegate, Signature signature)
   {
      this.delegate = delegate;
      this.signature = signature;
   }

   @Override
   public int read()
           throws IOException
   {
      int b = delegate.read();
      try
      {
         if (b > -1)
            signature.update((byte) b);
      }
      catch (SignatureException e)
      {
         throw new RuntimeException(e);
      }
      return b;
   }

   @Override
   public int read(byte[] bytes)
           throws IOException
   {
      int num = delegate.read(bytes);
      if (num > 0)
      {
         try
         {
            signature.update(bytes, 0, num);
         }
         catch (SignatureException e)
         {
            throw new RuntimeException(e);
         }
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
         try
         {
            signature.update(bytes, off, num);
         }
         catch (SignatureException e)
         {
            throw new RuntimeException(e);
         }
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
      throw new RuntimeException("Stream wrapped by Signature, cannot reset the stream without destroying signature");
   }

   @Override
   public boolean markSupported()
   {
      return delegate.markSupported();
   }
}
