package org.jboss.resteasy.client;

import org.jboss.resteasy.client.core.ClientInterceptorRepository;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.ClientInvokerModifier;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.net.URI;

/**
 * Helper class that allows you to pre-initialize an Executor, preset some ClientRequest attributes (like follow redirects)
 * and define some client-side interceptors you want applied
 *
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 * @author Solomon Duskis
 * @version $Revision: 1 $
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
 * @see org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
 */
@Deprecated
public class ClientRequestFactory
{
   private ResteasyProviderFactory providerFactory;
   private ClientExecutor executor;
   private URI base = null;
   private boolean applyDefaultInterceptors = false;
   private boolean followRedirects = false;
   private ClientInterceptorRepositoryImpl prefixInterceptors = new ClientInterceptorRepositoryImpl();
   private ClientInterceptorRepositoryImpl suffixInterceptors = new ClientInterceptorRepositoryImpl();

   public ClientRequestFactory()
   {
      init(null, null, null);
   }

   public ClientRequestFactory(URI base)
   {
      init(null, null, base);
   }

   public ClientRequestFactory(ClientExecutor executor, URI base)
   {
      init(executor, null, base);
   }

   public ClientRequestFactory(ClientExecutor executor,
                               ResteasyProviderFactory providerFactory)
   {
      init(executor, providerFactory, null);
   }

   public ClientRequestFactory(ClientExecutor executor,
                               ResteasyProviderFactory providerFactory, URI base)
   {
      init(executor, providerFactory, base);
   }

   private void init(ClientExecutor executor,
                     ResteasyProviderFactory providerFactory, URI base)
   {
      if (providerFactory == null)
         this.providerFactory = ResteasyProviderFactory.getInstance();
      else
         this.providerFactory = providerFactory;
      if (executor == null)
    	  this.executor = new ApacheHttpClient4Executor();
      else
         this.executor = executor;
      this.base = base;
   }

   public ClientRequestFactory(ClientRequestFactory other)
   {
      this.providerFactory = other.providerFactory;
      this.executor = other.executor;
      this.setBase(other.getBase());
      this.applyDefaultInterceptors = other.applyDefaultInterceptors;
      this.followRedirects = other.followRedirects;
      other.prefixInterceptors
              .copyClientInterceptorsTo(this.prefixInterceptors);
      other.suffixInterceptors
              .copyClientInterceptorsTo(this.suffixInterceptors);
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

   public ClientInterceptorRepository getPrefixInterceptors()
   {
      return prefixInterceptors;
   }

   public ClientInterceptorRepository getSuffixInterceptors()
   {
      return suffixInterceptors;
   }

   public boolean isFollowRedirects()
   {
      return followRedirects;
   }

   public void setFollowRedirects(boolean followRedirects)
   {
      this.followRedirects = followRedirects;
   }

   private void applyInterceptors(ClientInterceptorRepositoryImpl repository)
   {
      prefixInterceptors.prefixClientInterceptorsTo(repository);
      suffixInterceptors.copyClientInterceptorsTo(repository);
   }

   public ClientRequestFactory clone()
   {
      return new ClientRequestFactory(this);
   }

   public ClientRequest createRelativeRequest(String uriTemplate)
   {
      return createRequest(base.toString() + uriTemplate);
   }

   public ClientRequest createRequest(String uriTemplate)
   {
      ClientRequest clientRequest = new ClientRequest(new ResteasyUriBuilder()
              .uriTemplate(uriTemplate), executor, providerFactory);
      if (applyDefaultInterceptors)
      {
         ClientInvokerInterceptorFactory.applyDefaultInterceptors(
                 clientRequest, providerFactory);
      }
      if (followRedirects)
      {
         clientRequest.followRedirects();
      }
      applyInterceptors(clientRequest);
      return clientRequest;
   }

   public <T> T getRelative(String uriTemplate, Class<T> type, Object... params)
           throws Exception
   {
      return get(base.toString() + uriTemplate, type, params);
   }

   public <T> T get(String uriTemplate, Class<T> type, Object... params)
           throws Exception
   {
      return createRequest(uriTemplate).followRedirects(true).pathParameters(
              params).get(type).getEntity();
   }

   public <T> T createProxy(Class<T> clazz)
   {
      assert base != null;
      return createProxy(clazz, base);
   }

   public <T> T createProxy(Class<T> clazz, String baseUri)
   {
      return createProxy(clazz, URI.create(baseUri));
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
