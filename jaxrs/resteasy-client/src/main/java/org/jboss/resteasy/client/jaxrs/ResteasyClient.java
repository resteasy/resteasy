package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
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
   protected volatile ResteasyProviderFactory providerFactory;
   protected volatile ClientHttpEngine httpEngine;
   protected volatile ExecutorService asyncInvocationExecutor;
   protected ClientConfiguration configuration = new ClientConfiguration();

   public ResteasyProviderFactory providerFactory()
   {
      ResteasyProviderFactory result = providerFactory;
      if (result == null)
      { // First check (no locking)
         synchronized (this)
         {
            result = providerFactory;
            if (result == null) // Second check (with locking)
               providerFactory = result = ResteasyProviderFactory.getInstance();
         }
      }
      return result;
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
   public WebTarget target(String uri) throws IllegalArgumentException, NullPointerException
   {
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public WebTarget target(URI uri) throws NullPointerException
   {
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public WebTarget target(UriBuilder uriBuilder) throws NullPointerException
   {
      return new ClientWebTarget(this, uriBuilder, configuration);
   }

   @Override
   public WebTarget target(Link link) throws NullPointerException
   {
      URI uri = link.getUri();
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public Invocation invocation(Link link) throws NullPointerException, IllegalArgumentException
   {
      if (link.getMethod() == null) throw new IllegalArgumentException("Link must have a method attribute in order to build an Invocation");
      WebTarget target = target(link);
      return target.request(link.getProduces().toArray(new String[link.getProduces().size()])).build(link.getMethod());
   }

   @Override
   public Invocation invocation(Link link, Entity<?> entity) throws NullPointerException, IllegalArgumentException
   {
      if (link.getMethod() == null) throw new IllegalArgumentException("Link must have a method attribute in order to build an Invocation");
      WebTarget target = target(link);
      return target.request(link.getProduces().toArray(new String[link.getProduces().size()])).build(link.getMethod(), entity);
   }
}
