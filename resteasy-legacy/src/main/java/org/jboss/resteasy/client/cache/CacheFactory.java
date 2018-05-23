package org.jboss.resteasy.client.cache;

import org.jboss.resteasy.client.core.ClientInterceptorRepository;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerModifier;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated Caching in the Resteasy client framework in resteasy-jaxrs is replaced by 
 * caching in the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 */
@Deprecated
public class CacheFactory
{
   /**
    * Makes the client proxy cacheable. Returns the cache that will hold
    * returned values from the server.
    *
    * @param clientProxy client proxy
    * @return browser cache
    */
   public static LightweightBrowserCache makeCacheable(Object clientProxy)
   {
      LightweightBrowserCache cache = new LightweightBrowserCache();
      makeCacheable(clientProxy, cache);
      return cache;
   }

   /**
    * Makes the client proxy cacheable. This method allows you to pass in a
    * shared cache that the proxy should use
    *
    * @param clientProxy client proxy
    * @param cache browser cache
    */
   public static void makeCacheable(Object clientProxy, BrowserCache cache)
   {
      final CacheInterceptor interceptor = new CacheInterceptor(cache);
      ResteasyClientProxy proxy = (ResteasyClientProxy) clientProxy;
      proxy.applyClientInvokerModifier(new ClientInvokerModifier()
      {
         public void modify(ClientInvoker invoker)
         {
            if (invoker.getHttpMethod().equalsIgnoreCase("GET"))
            {
               invoker.getExecutionInterceptorList().addFirst(interceptor);
            }
         }
      });
   }

   /**
    * Make a raw ClientRequest cache results in the provided cache.
    *
    * @param interceptorRepository interceptor repository
    * @param cache browser cache
    */
   public static void makeCacheable(
           ClientInterceptorRepository interceptorRepository, BrowserCache cache)
   {
      interceptorRepository.getExecutionInterceptorList().addFirst(
              new CacheInterceptor(cache));
   }
}
