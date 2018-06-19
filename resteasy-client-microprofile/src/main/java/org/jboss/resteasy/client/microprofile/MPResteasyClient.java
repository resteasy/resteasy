package org.jboss.resteasy.client.microprofile;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

public class MPResteasyClient extends ResteasyClient
{
   protected MPResteasyClient(ClientHttpEngine httpEngine, ExecutorService asyncInvocationExecutor, boolean cleanupExecutor,
         ScheduledExecutorService scheduledExecutorService, ClientConfiguration configuration)
   {
      super(httpEngine, asyncInvocationExecutor, cleanupExecutor, scheduledExecutorService, configuration);
   }
   
   protected MPResteasyClient(ClientHttpEngine httpEngine, ExecutorService asyncInvocationExecutor,
         boolean cleanupExecutor, ClientConfiguration configuration)
   {
      super(httpEngine, asyncInvocationExecutor, cleanupExecutor, configuration);
   }

   @Override
   public ResteasyWebTarget target(String uri) throws IllegalArgumentException, NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException(Messages.MESSAGES.uriWasNull());
      return new MPClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(URI uri) throws NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException(Messages.MESSAGES.uriWasNull());
      return new MPClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(UriBuilder uriBuilder) throws NullPointerException
   {
      abortIfClosed();
      if (uriBuilder == null) throw new NullPointerException(Messages.MESSAGES.uriBuilderWasNull());
      return new MPClientWebTarget(this, uriBuilder, configuration);
   }

   @Override
   public ResteasyWebTarget target(Link link) throws NullPointerException
   {
      abortIfClosed();
      if (link == null) throw new NullPointerException(Messages.MESSAGES.linkWasNull());
      URI uri = link.getUri();
      return new MPClientWebTarget(this, uri, configuration);
   }

}
