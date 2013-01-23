package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyClient implements Client
{
   protected volatile ClientHttpEngine httpEngine;
   protected volatile ExecutorService asyncInvocationExecutor;
   protected ClientConfiguration configuration;


   ResteasyClient()
   {
      this(ResteasyProviderFactory.getInstance());
   }

   ResteasyClient(ResteasyProviderFactory factory)
   {
      configuration = new ClientConfiguration(factory);
      httpEngine = new ApacheHttpClient4Engine(configuration());
      asyncInvocationExecutor = Executors.newFixedThreadPool(10);
   }

   ResteasyClient(ClientHttpEngine httpEngine)
   {
      this.httpEngine = httpEngine;
      configuration = new ClientConfiguration(ResteasyProviderFactory.getInstance());
      asyncInvocationExecutor = Executors.newFixedThreadPool(10);

   }

   ResteasyClient(ClientHttpEngine httpEngine, ExecutorService asyncInvocationExecutor, ClientConfiguration configuration)
   {
      this.httpEngine = httpEngine;
      this.asyncInvocationExecutor = asyncInvocationExecutor;
      this.configuration = configuration;
   }

   public ClientHttpEngine httpEngine()
   {
      return httpEngine;
   }

   public ExecutorService asyncInvocationExecutor()
   {
      return asyncInvocationExecutor;
   }

   @Override
   public void close()
   {
      try
      {
         httpEngine.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Configuration configuration()
   {
      return configuration;
   }

   @Override
   public ResteasyWebTarget target(String uri) throws IllegalArgumentException, NullPointerException
   {
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(URI uri) throws NullPointerException
   {
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(UriBuilder uriBuilder) throws NullPointerException
   {
      return new ClientWebTarget(this, uriBuilder, configuration);
   }

   @Override
   public ResteasyWebTarget target(Link link) throws NullPointerException
   {
      URI uri = link.getUri();
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public Invocation.Builder invocation(Link link) throws NullPointerException, IllegalArgumentException
   {
      WebTarget target = target(link);
      return target.request(link.getType());
   }
}
