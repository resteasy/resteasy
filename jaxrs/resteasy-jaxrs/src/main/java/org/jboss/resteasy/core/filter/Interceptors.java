package org.jboss.resteasy.core.filter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.RequestFilter;
import javax.ws.rs.ext.ResponseFilter;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Interceptors
{
   private FilterRegistry<RequestFilter> requestFilters;
   private FilterRegistry<ResponseFilter> responseFilters;
   private FilterRegistry<ReaderInterceptor> readerInterceptors;
   private FilterRegistry<WriterInterceptor> writerInterceptors;
   private ResteasyProviderFactory providerFactory;

   public Interceptors(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      requestFilters = new FilterRegistry<RequestFilter>(RequestFilter.class, providerFactory);
      responseFilters = new FilterRegistry<ResponseFilter>(ResponseFilter.class, providerFactory);
      readerInterceptors = new FilterRegistry<ReaderInterceptor>(ReaderInterceptor.class, providerFactory);
      writerInterceptors = new FilterRegistry<WriterInterceptor>(WriterInterceptor.class, providerFactory);
   }

   public FilterRegistry<RequestFilter> getRequestFilters()
   {
      return requestFilters;
   }

   public FilterRegistry<ResponseFilter> getResponseFilters()
   {
      return responseFilters;
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
