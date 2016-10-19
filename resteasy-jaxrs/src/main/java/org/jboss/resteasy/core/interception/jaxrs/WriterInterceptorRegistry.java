package org.jboss.resteasy.core.interception.jaxrs;

import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterInterceptorRegistry extends JaxrsInterceptorRegistry<WriterInterceptor>
{
   public WriterInterceptorRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, WriterInterceptor.class);
   }

   public WriterInterceptorRegistry clone(ResteasyProviderFactory factory)
   {
      WriterInterceptorRegistry clone = new WriterInterceptorRegistry(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
