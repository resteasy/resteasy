package org.jboss.resteasy.client.core;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Method;

/**
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy interceptor facility introduced in release 2.x
 *             is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
@SuppressWarnings("unchecked")
public class ClientInvokerInterceptorFactory
{
   public static void applyDefaultInterceptors(
           ClientInterceptorRepositoryImpl repository,
           ResteasyProviderFactory providerFactory)
   {
      applyDefaultInterceptors(repository, providerFactory, null, null);
   }

   public static void applyDefaultInterceptors(
           ClientInterceptorRepositoryImpl repository,
           ResteasyProviderFactory providerFactory, Class declaring, Method method)
   {
      repository.setReaderInterceptors(providerFactory
              .getClientReaderInterceptorRegistry().postMatch(declaring,
                      method));
      repository.setWriterInterceptors(providerFactory
              .getClientWriterInterceptorRegistry().postMatch(declaring,
                      method));
      repository.setExecutionInterceptors(providerFactory
              .getClientExecutionInterceptorRegistry().bind(declaring, method));
   }

}
