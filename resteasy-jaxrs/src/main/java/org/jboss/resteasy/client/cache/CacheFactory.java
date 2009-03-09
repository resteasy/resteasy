package org.jboss.resteasy.client.cache;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ResteasyClientProxy;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheFactory
{
   /**
    * Makes the client proxy cacheable.  Returns the cache that will hold returned values from the server.
    *
    * @param clientProxy
    * @return
    */
   public static LightweightBrowserCache makeCacheable(Object clientProxy)
   {
      LightweightBrowserCache cache = new LightweightBrowserCache();
      makeCacheable(clientProxy, cache);
      return cache;
   }

   public static void makeCacheable(Object clientProxy, BrowserCache cache)
   {
      ResteasyClientProxy proxy = (ResteasyClientProxy) clientProxy;
      CacheInterceptor interceptor = new CacheInterceptor(cache);

      for (ClientInvoker invoker : proxy.getResteasyClientInvokers())
      {
         if (invoker.getHttpMethod().equalsIgnoreCase("GET"))
         {
            if (invoker.getExecutionInterceptors() == null)
            {
               ClientExecutionInterceptor[] interceptors = {interceptor};
               invoker.setExecutionInterceptors(interceptors);
            }
            else
            {
               ClientExecutionInterceptor[] interceptors = new ClientExecutionInterceptor[invoker.getExecutionInterceptors().length + 1];
               System.arraycopy(invoker.getExecutionInterceptors(), 0, interceptors, 1, invoker.getExecutionInterceptors().length);
               interceptors[0] = interceptor;
               invoker.setExecutionInterceptors(interceptors);
            }
         }
      }
   }

   public static void makeCacheable(ClientRequest request, BrowserCache cache)
   {
      CacheInterceptor interceptor = new CacheInterceptor(cache);
      if (request.getExecutionInterceptors() == null)
      {
         ClientExecutionInterceptor[] interceptors = {interceptor};
         interceptors[0] = interceptor;
         request.setExecutionInterceptors(interceptors);
      }
      else
      {
         ClientExecutionInterceptor[] interceptors = new ClientExecutionInterceptor[request.getExecutionInterceptors().length + 1];
         System.arraycopy(request.getExecutionInterceptors(), 0, interceptors, 1, request.getExecutionInterceptors().length);
         interceptors[0] = interceptor;
         request.setExecutionInterceptors(interceptors);
      }
   }


}
