package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import jakarta.ws.rs.ext.ReaderInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderInterceptorRegistryImpl extends JaxrsInterceptorRegistryImpl<ReaderInterceptor>
{
   public ReaderInterceptorRegistryImpl(final ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ReaderInterceptor.class);
   }

   public ReaderInterceptorRegistryImpl clone(ResteasyProviderFactory factory)
   {
      ReaderInterceptorRegistryImpl clone = new ReaderInterceptorRegistryImpl(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
