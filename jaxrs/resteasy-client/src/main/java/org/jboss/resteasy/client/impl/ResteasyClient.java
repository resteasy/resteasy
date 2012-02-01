package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Target;
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
   protected volatile ExecutorService executor;
   protected ClientConfiguration configuration = new ClientConfiguration();

   public ResteasyProviderFactory getProviderFactory()
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

   public ClientHttpEngine getHttpEngine()
   {
      ClientHttpEngine result = httpEngine;
      if (result == null)
      { // First check (no locking)
         synchronized (this)
         {
            result = httpEngine;
            if (result == null) // Second check (with locking)
               httpEngine = result = new ApacheHttpClient4Engine(configuration);
         }
      }
      return result;
   }

   public ExecutorService getExecutorService()
   {
      ExecutorService result = executor;
      if (result == null)
      { // First check (no locking)
         synchronized (this)
         {
            result = executor;
            if (result == null) // Second check (with locking)
               executor = result = Executors.newFixedThreadPool(10);
         }
      }
      return result;
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
   public Target target(String uri) throws IllegalArgumentException, NullPointerException
   {
      return new ClientTarget(uri, getProviderFactory(), getHttpEngine(), getExecutorService(), configuration);
   }

   @Override
   public Target target(URI uri) throws NullPointerException
   {
      return new ClientTarget(uri, getProviderFactory(), getHttpEngine(), getExecutorService(), configuration);
   }

   @Override
   public Target target(UriBuilder uriBuilder) throws NullPointerException
   {
      return new ClientTarget(uriBuilder, getProviderFactory(), getHttpEngine(), getExecutorService(), configuration);
   }

   @Override
   public Target target(Link link) throws NullPointerException
   {
      URI uri = link.getUri();
      return new ClientTarget(uri, getProviderFactory(), getHttpEngine(), getExecutorService(), configuration);
   }

   @Override
   public Invocation invocation(Link link) throws NullPointerException, IllegalArgumentException
   {
      if (link.getMethod() == null) throw new IllegalArgumentException("Link must have a method attribute in order to build an Invocation");
      Target target = target(link);
      return target.request(link.getProduces().toArray(new String[link.getProduces().size()])).build(link.getMethod());
   }

   @Override
   public Invocation invocation(Link link, Entity<?> entity) throws NullPointerException, IllegalArgumentException
   {
      if (link.getMethod() == null) throw new IllegalArgumentException("Link must have a method attribute in order to build an Invocation");
      Target target = target(link);
      return target.request(link.getProduces().toArray(new String[link.getProduces().size()])).build(link.getMethod(), entity);
   }
}
