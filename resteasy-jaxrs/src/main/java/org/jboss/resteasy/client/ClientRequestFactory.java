package org.jboss.resteasy.client;

import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.ApacheHttpClientExecutor;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.ClientInvokerModifier;
import org.jboss.resteasy.client.core.ResteasyClientProxy;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ClientRequestFactory
{
   private ResteasyProviderFactory providerFactory;
   private ClientExecutor executor;
   private boolean applyDefaultInterceptors = false;
   private ClientInterceptorRepositoryImpl prefixInterceptors = new ClientInterceptorRepositoryImpl();
   private ClientInterceptorRepositoryImpl suffixInterceptors = new ClientInterceptorRepositoryImpl();
   private URI base = null;

   ClientRequestFactory()
   {
      this(new HttpClient());
   }

   public ClientRequestFactory(HttpClient httpClient)
   {
      this(httpClient, ResteasyProviderFactory.getInstance());
   }

   public ClientRequestFactory(HttpClient httpClient,
         ResteasyProviderFactory instance)
   {
      this(new ApacheHttpClientExecutor(httpClient), instance);
   }

   public ClientRequestFactory(ClientExecutor executor,
         ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.executor = executor;
   }

   public URI getBase()
   {
      return base;
   }

   public void setBase(URI base)
   {
      this.base = base;
   }

   public void enableDefaultInterceptors()
   {
      applyDefaultInterceptors = true;
   }

   public ClientInterceptorRepositoryImpl getPrefixInterceptors()
   {
      return prefixInterceptors;
   }

   public ClientInterceptorRepositoryImpl getSuffixInterceptors()
   {
      return suffixInterceptors;
   }

   public ClientRequest createRequest(String uriTemplate)
   {
      ClientRequest clientRequest = new ClientRequest(new UriBuilderImpl()
            .uriTemplate(uriTemplate), executor, providerFactory);
      if (applyDefaultInterceptors)
      {
         ClientInvokerInterceptorFactory.applyDefaultInterceptors(
               clientRequest, providerFactory);
      }
      applyInterceptors(clientRequest);
      return clientRequest;
   }

   public <T> T get(String uriTemplate, Class<T> type, Object... params)
         throws Exception
   {
      ClientRequest clientRequest = createRequest(uriTemplate);
      clientRequest.pathParameters(params);
      return clientRequest.get(type).getEntity();
   }

   private void applyInterceptors(ClientInterceptorRepositoryImpl repository)
   {
      prefixInterceptors.prefixClientInterceptorsTo(repository);
      suffixInterceptors.copyClientInterceptorsTo(repository);
   }

   public <T> T createProxy(Class<T> clazz)
   {
      assert base != null;
      return createProxy(clazz, base);
   }

   public <T> T createProxy(Class<T> clazz, String baseUri)
   {
      return createProxy(clazz, ProxyFactory.createUri(baseUri));
   }

   public <T> T createProxy(Class<T> clazz, URI baseUri)
   {
      // right now all proxies get the default interceptors
      T proxy = ProxyFactory.create(clazz, baseUri, executor, providerFactory);
      ResteasyClientProxy clientProxy = (ResteasyClientProxy) proxy;
      clientProxy.applyClientInvokerModifier(new ClientInvokerModifier()
      {
         public void modify(ClientInvoker invoker)
         {
            applyInterceptors(invoker);
         }
      });
      return proxy;
   }
}
