package org.jboss.resteasy.core.interception.jaxrs;

import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterInterceptorRegistryImpl extends JaxrsInterceptorRegistryImpl<WriterInterceptor>
{
   public WriterInterceptorRegistryImpl(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, WriterInterceptor.class);
   }

   public WriterInterceptorRegistryImpl clone(ResteasyProviderFactory factory)
   {
      WriterInterceptorRegistryImpl clone = new WriterInterceptorRegistryImpl(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
