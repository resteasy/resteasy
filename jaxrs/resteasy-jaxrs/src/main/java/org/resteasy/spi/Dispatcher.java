package org.resteasy.spi;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Dispatcher
{
   ResteasyProviderFactory getProviderFactory();

   Registry getRegistry();

   void setProviderFactory(ResteasyProviderFactory providerFactory);

   void setRegistry(Registry registry);

   void invoke(HttpRequest in, HttpResponse response);
}
