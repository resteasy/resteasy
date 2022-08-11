package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.util.CommitHeaderOutputStream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class GZIPEncodingInterceptor implements WriterInterceptor
{
   public static class EndableGZIPOutputStream extends GZIPOutputStream
   {
      public EndableGZIPOutputStream(final OutputStream os) throws IOException
      {
         super(os);
      }

      @Override
      public void finish() throws IOException
      {
         super.finish();
         def.end(); // make sure on finish the deflater's end() is called to release the native code pointer
      }
   }

   public static class CommittedGZIPOutputStream extends CommitHeaderOutputStream
   {
      protected CommittedGZIPOutputStream(final OutputStream delegate, final CommitCallback headers)
      {
         super(delegate, headers);
      }

      protected GZIPOutputStream gzip;

      public GZIPOutputStream getGzip()
      {
         return gzip;
      }

      @Override
      public synchronized void commit()
      {
         if (isHeadersCommitted) return;
         isHeadersCommitted = true;
         try
         {
            // GZIPOutputStream constructor writes to underlying OS causing headers to be written.
            // so we swap gzip OS in when we are ready to write.
            gzip  = new EndableGZIPOutputStream(delegate);
            delegate = gzip;
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());

      Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

      if (encoding != null && encoding.toString().equalsIgnoreCase("gzip"))
      {
         OutputStream old = context.getOutputStream();
         // GZIPOutputStream constructor writes to underlying OS causing headers to be written.
         CommittedGZIPOutputStream gzipOutputStream = new CommittedGZIPOutputStream(old, null);

         // Any content length set will be obsolete
         context.getHeaders().remove("Content-Length");

         context.setOutputStream(gzipOutputStream);
         try
         {
            context.proceed();
         }
         finally
         {
            if (gzipOutputStream.getGzip() != null) gzipOutputStream.getGzip().finish();
            context.setOutputStream(old);
         }
         return;
      }
      else
      {
         context.proceed();
      }
   }
}
