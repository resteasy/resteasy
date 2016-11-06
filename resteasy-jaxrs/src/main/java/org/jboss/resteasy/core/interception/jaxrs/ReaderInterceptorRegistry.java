package org.jboss.resteasy.core.interception.jaxrs;

import javax.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderInterceptorRegistry extends JaxrsInterceptorRegistry<ReaderInterceptor>
{
   public ReaderInterceptorRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ReaderInterceptor.class);
   }

   public ReaderInterceptorRegistry clone(ResteasyProviderFactory factory)
   {
      ReaderInterceptorRegistry clone = new ReaderInterceptorRegistry(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
