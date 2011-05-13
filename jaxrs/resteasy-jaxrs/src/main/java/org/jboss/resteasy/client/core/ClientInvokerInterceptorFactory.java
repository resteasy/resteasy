package org.jboss.resteasy.client.core;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Method;

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
              .getClientMessageBodyReaderInterceptorRegistry().bind(declaring,
                      method));
      repository.setWriterInterceptors(providerFactory
              .getClientMessageBodyWriterInterceptorRegistry().bind(declaring,
                      method));
      repository.setExecutionInterceptors(providerFactory
              .getClientExecutionInterceptorRegistry().bind(declaring, method));
   }

}
