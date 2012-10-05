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

   public ResteasyClient()
   {
      configuration = new ClientConfiguration(ResteasyProviderFactory.getInstance());
   }

   public ResteasyClient(ResteasyProviderFactory providerFactory)
   {
      configuration = new ClientConfiguration(providerFactory);
   }

   public ClientHttpEngine httpEngine()
   {
      ClientHttpEngine result = httpEngine;
      if (result == null)
      { // First check (no locking)
         synchronized (this)
         {
            result = httpEngine;
            if (result == null) // Second check (with locking)
            {
               httpEngine = result = new ApacheHttpClient4Engine(configuration());
            }
         }
      }
      return result;
   }

   public ExecutorService asyncInvocationExecutor()
   {
      ExecutorService result = asyncInvocationExecutor;
      if (result == null)
      { // First check (no locking)
         synchronized (this)
         {
            result = asyncInvocationExecutor;
            if (result == null) // Second check (with locking)
               asyncInvocationExecutor = result = Executors.newFixedThreadPool(10);
         }
      }
      return result;
   }

   public ResteasyClient httpEngine(ClientHttpEngine httpEngine)
   {
      this.httpEngine = httpEngine;
      return this;
   }

   public ResteasyClient asyncInvocationExecutor(ExecutorService asyncInvocationExecutor)
   {
      this.asyncInvocationExecutor = asyncInvocationExecutor;
      return this;
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
