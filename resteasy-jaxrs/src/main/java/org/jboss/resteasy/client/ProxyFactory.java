package org.jboss.resteasy.client;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.ClientInterceptor;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyFactory
{

   private static Collection<ClientInterceptor> interceptors = new ArrayList<ClientInterceptor>();

   public static <T> T create(Class<T> clazz, String base)
   {
      return create(clazz, base, new HttpClient());
   }

   public static <T> T create(Class<T> clazz, String base, HttpClient client)
   {
      try
      {
         return create(clazz, new URI(base), client, ResteasyProviderFactory.getInstance());
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T> T create(Class<T> clazz, URI baseUri, HttpClient httpClient, ResteasyProviderFactory providerFactory)
   {
      return create(clazz, baseUri, httpClient, providerFactory, ProxyFactory.interceptors);
   }

   @SuppressWarnings("unchecked")
   public static <T> T create(Class<T> clazz, URI baseUri, HttpClient httpClient, ResteasyProviderFactory providerFactory, Collection<ClientInterceptor> interceptors)
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
         if (httpMethods == null)
            throw new RuntimeException("Method must be annotated with an http method annotation @GET, etc..");
         if (httpMethods.size() != 1)
            throw new RuntimeException("You may only annotate a method with only one http method annotation");

         invoker = new ClientInvoker(clazz, method, providerFactory, httpClient, interceptors);
         invoker.setBaseUri(baseUri);
         invoker.setRestVerb(getRestVerb(httpMethods));
         methodMap.put(method, invoker);
      }

      Class<?>[] intfs = {clazz};

      ClientProxy clientProxy = new ClientProxy(methodMap);
      // this is done so that equals and hashCode work ok. Adding the proxy to a
      // Collection will cause equals and hashCode to be invoked. The Spring
      // infrastructure had some problems without this.
      clientProxy.setClazz(clazz);

      return (T) Proxy.newProxyInstance(clazz.getClassLoader(), intfs, clientProxy);
   }

   private static String getRestVerb(Set<String> httpMethods)
   {
      if (httpMethods.contains(HttpMethod.GET))
         return "GET";
      else if (httpMethods.contains(HttpMethod.PUT))
         return "PUT";
      else if (httpMethods.contains(HttpMethod.POST))
         return "POST";
      else if (httpMethods.contains(HttpMethod.DELETE))
         return "DELETE";
      else
         throw new RuntimeException("@" + httpMethods.iterator().next() + " is not supported yet");
   }

   public static void addInterceptor(ClientInterceptor clientInterceptor)
   {
      interceptors.add(clientInterceptor);
   }
}
