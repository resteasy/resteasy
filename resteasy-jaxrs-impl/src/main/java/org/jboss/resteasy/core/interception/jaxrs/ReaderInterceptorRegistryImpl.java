package org.jboss.resteasy.core.interception.jaxrs;

import javax.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderInterceptorRegistryImpl extends JaxrsInterceptorRegistryImpl<ReaderInterceptor>
{
   public ReaderInterceptorRegistryImpl(ResteasyProviderFactory providerFactory)
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
