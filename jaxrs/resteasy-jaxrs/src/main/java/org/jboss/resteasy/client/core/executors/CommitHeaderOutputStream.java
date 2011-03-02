package org.jboss.resteasy.client.core.executors;

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
   protected Headers headers;

   public CommitHeaderOutputStream(OutputStream delegate, Headers headers)
   {
      this.delegate = delegate;
      this.headers = headers;
   }

   public interface Headers
   {
      void commit();
   }

   public void commit()
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
