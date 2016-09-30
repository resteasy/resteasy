package org.jboss.resteasy.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CommitHeaderOutputStream extends OutputStream
{
   protected OutputStream delegate;
   protected boolean isHeadersCommitted;
   protected CommitCallback headers;

   public CommitHeaderOutputStream(OutputStream delegate, CommitCallback headers)
   {
      this.delegate = delegate;
      this.headers = headers;
   }

   public CommitHeaderOutputStream()
   {
   }

   public OutputStream getDelegate()
   {
      return delegate;
   }

   public void setDelegate(OutputStream delegate)
   {
      this.delegate = delegate;
   }

   public void setHeaders(CommitCallback headers)
   {
      this.headers = headers;
   }

   public interface CommitCallback
   {
      void commit();
   }

   public synchronized void commit()
   {
      if (isHeadersCommitted) return;
      isHeadersCommitted = true;
      headers.commit();
   }

   @Override
   public void write(int i) throws IOException
   {
      commit();
      delegate.write(i);
   }

   @Override
   public void write(byte[] bytes) throws IOException
   {
      commit();
      delegate.write(bytes);
   }

   @Override
   public void write(byte[] bytes, int i, int i1) throws IOException
   {
      commit();
      delegate.write(bytes, i, i1);
   }

   @Override
   public void flush() throws IOException
   {
      commit();
      delegate.flush();
   }



   @Override
   public void close() throws IOException
   {
      commit();
      delegate.close();
   }
}
