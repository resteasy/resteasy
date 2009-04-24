package org.jboss.resteasy.client;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyFactory
{

   public static <T> T create(Class<T> clazz, String base)
   {
      return create(clazz, base, new HttpClient());
   }

   public static <T> T create(Class<T> clazz, String base, HttpClient client)
   {
   	return create(clazz, createUri(base), client, ResteasyProviderFactory.getInstance());
   }

	public static URI createUri(String base)
	{
      try
      {
         return new URI(base);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
	}

   public static <T> T create(Class<T> clazz, URI baseUri, HttpClient httpClient, ResteasyProviderFactory providerFactory)
   {
   	return create(clazz, baseUri, new ApacheHttpClientExecutor(httpClient), providerFactory);
   }
   
   @SuppressWarnings("unchecked")
   public static <T> T create(Class<T> clazz, URI baseUri, ClientExecutor executor, ResteasyProviderFactory providerFactory)
   {
      HashMap<Method, ClientInvoker> methodMap = new HashMap<Method, ClientInvoker>();

      if (providerFactory instanceof ProviderFactoryDelegate)
      {
         providerFactory = ((ProviderFactoryDelegate) providerFactory).getDelegate();
      }

      for (Method method : clazz.getMethods())
      {
         ClientInvoker invoker = null;
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (httpMethods.size() != 1)
            throw new RuntimeException("You must use at least one, but no more than one http method annotation");

         invoker = new ClientInvoker(baseUri, clazz, method, providerFactory, executor);
         ClientInvokerInterceptorFactory.applyDefaultInterceptors(invoker, providerFactory, clazz, method);
         invoker.setHttpMethod(httpMethods.iterator().next());
         methodMap.put(method, invoker);
      }

      Class<?>[] intfs = {clazz, ResteasyClientProxy.class};

      ClientProxy clientProxy = new ClientProxy(methodMap);
      // this is done so that equals and hashCode work ok. Adding the proxy to a
      // Collection will cause equals and hashCode to be invoked. The Spring
      // infrastructure had some problems without this.
      clientProxy.setClazz(clazz);

      return (T) Proxy.newProxyInstance(clazz.getClassLoader(), intfs, clientProxy);
   }

}
