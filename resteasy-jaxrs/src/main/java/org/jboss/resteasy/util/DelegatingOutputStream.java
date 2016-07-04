package org.jboss.resteasy.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DelegatingOutputStream extends OutputStream
{
   protected OutputStream delegate;

   public DelegatingOutputStream()
   {
   }

   public DelegatingOutputStream(OutputStream delegate)
   {
      this.delegate = delegate;
   }

   public OutputStream getDelegate()
   {
      return delegate;
   }

   public void setDelegate(OutputStream delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public void write(int i) throws IOException
   {
      getDelegate().write(i);
   }

   @Override
   public void write(byte[] bytes) throws IOException
   {
      getDelegate().write(bytes);
   }

   @Override
   public void write(byte[] bytes, int i, int i1) throws IOException
   {
      getDelegate().write(bytes, i, i1);
   }

   @Override
   public void flush() throws IOException
   {
      getDelegate().flush();
   }

   @Override
   public void close() throws IOException
   {
      getDelegate().close();
   }
}
