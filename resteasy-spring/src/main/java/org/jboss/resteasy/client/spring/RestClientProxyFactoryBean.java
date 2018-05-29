package org.jboss.resteasy.client.spring;

import org.apache.http.client.HttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.URI;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to generate a client
 * proxy from a REST annotated interface.
 * <p>
 * Example: The following spring xml configuration snippet makes a bean with the
 * id echoClient. The bean is a generated proxy of the a.b.c.Echo interface to
 * access the remote service on http://server.far.far.away:8080/echo base URI.
 * </p>
 * <pre>
 * &lt;bean id=&quot;echoClient&quot; class=&quot;org.jboss.resteasy.client.spring.RestClientProxyFactoryBean&quot;
 * p:serviceInterface=&quot;a.b.c.Echo&quot; p:baseUri=&quot;http://server.far.far.away:8080/echo&quot; /&gt;
 * </pre>
 *
 * @author Attila Kiraly
 * @param <T> The type representing the client interface.
 */
public class RestClientProxyFactoryBean<T> implements FactoryBean<T>,
        InitializingBean
{
   private Class<T> serviceInterface;
   private URI baseUri;
   private T client;
   private HttpClient httpClient;
   private ClientExecutor clientExecutor;
   private ResteasyProviderFactory resteasyProviderFactory;

   /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
   public T getObject() throws Exception
   {
      return client;
   }

   /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
   public Class<T> getObjectType()
   {
      return serviceInterface;
   }

   /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
   public boolean isSingleton()
   {
      return true;
   }

   /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
   public void afterPropertiesSet() throws Exception
   {
      if (resteasyProviderFactory == null)
         resteasyProviderFactory = ResteasyProviderFactory.getInstance();
      RegisterBuiltin.register(resteasyProviderFactory);

      if (clientExecutor == null)
      {
         if (httpClient == null)
         {
            clientExecutor = new ApacheHttpClient4Executor();
         }
         else
         {
            clientExecutor = new ApacheHttpClient4Executor(httpClient);
         }
         client = ProxyFactory.create(serviceInterface, baseUri, clientExecutor,
                 resteasyProviderFactory);
      }
      else
         client = ProxyFactory.create(serviceInterface, baseUri,
                 clientExecutor, resteasyProviderFactory);
   }

   public Class<T> getServiceInterface()
   {
      return serviceInterface;
   }

   /**
    * This is a mandatory property that needs to be set.
    *
    * @param serviceInterface the interface for which a proxy is needed to be generated.
    */
   public void setServiceInterface(Class<T> serviceInterface)
   {
      this.serviceInterface = serviceInterface;
   }

   public URI getBaseUri()
   {
      return baseUri;
   }

   /**
    * This is a mandatory property that needs to be set.
    *
    * @param baseUri the remote service base address.
    */
   public void setBaseUri(URI baseUri)
   {
      this.baseUri = baseUri;
   }

   public HttpClient getHttpClient()
   {
      return httpClient;
   }

   /**
    * Optional property. If this property is set and {@link #clientExecutor} is
    * null, this will be used by proxy generation. This could be useful for
    * example when you want to use a
    * {@link org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager}
    * instead of a
    * {@link org.apache.http.impl.conn.SingleClientConnManager} which
    * is the default in {@link org.apache.http.client.HttpClient}.
    *
    * @param httpClient the instance to be used by proxy generation
    */
   public void setHttpClient(HttpClient httpClient)
   {
      this.httpClient = httpClient;
   }

   public ClientExecutor getClientExecutor()
   {
      return clientExecutor;
   }

   /**
    * Optional property for advanced usage. If this property is set it will be
    * used by proxy generation. If this property is set the {@link #httpClient}
    * property is ignored.
    *
    * @param clientExecutor the instance to be used by proxy generation
    */
   public void setClientExecutor(ClientExecutor clientExecutor)
   {
      this.clientExecutor = clientExecutor;
   }

   public ResteasyProviderFactory getResteasyProviderFactory()
   {
      return resteasyProviderFactory;
   }

   /**
    * Optional property for advanced usage. For the most cases this property is
    * not needed to be set.
    *
    * @param resteasyProviderFactory the instance to be used by proxy generation.
    */
   public void setResteasyProviderFactory(
           ResteasyProviderFactory resteasyProviderFactory)
   {
      this.resteasyProviderFactory = resteasyProviderFactory;
   }

}
