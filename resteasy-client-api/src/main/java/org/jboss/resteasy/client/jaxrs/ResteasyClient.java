package org.jboss.resteasy.client.jaxrs;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

public interface ResteasyClient extends Client
{
   @Override
   ResteasyWebTarget target(URI uri);

   @Override
   ResteasyWebTarget target(String uri);

   @Override
   ResteasyWebTarget target(UriBuilder uriBuilder);

   @Override
   ResteasyWebTarget target(Link link);

   ClientHttpEngine httpEngine();

   ExecutorService asyncInvocationExecutor();

   ScheduledExecutorService getScheduledExecutor();

   void abortIfClosed();

   boolean isClosed();

}
