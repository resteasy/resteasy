package org.jboss.resteasy.core.filter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Interceptors
{
   private FilterRegistry<ReaderInterceptor> readerInterceptors;
   private FilterRegistry<WriterInterceptor> writerInterceptors;
   private ResteasyProviderFactory providerFactory;

   public Interceptors(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      readerInterceptors = new FilterRegistry<ReaderInterceptor>(ReaderInterceptor.class, providerFactory);
      writerInterceptors = new FilterRegistry<WriterInterceptor>(WriterInterceptor.class, providerFactory);
   }

   public FilterRegistry<ReaderInterceptor> getReaderInterceptors()
   {
      return readerInterceptors;
   }

   public FilterRegistry<WriterInterceptor> getWriterInterceptors()
   {
      return writerInterceptors;
   }
}
