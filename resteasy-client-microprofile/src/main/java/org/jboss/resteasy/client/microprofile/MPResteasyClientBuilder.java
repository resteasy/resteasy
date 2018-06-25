package org.jboss.resteasy.client.microprofile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

/**
 * An extension of ResteasyClientBuilder for implementing MP REST Client
 * 
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 *
 */
public class MPResteasyClientBuilder extends ResteasyClientBuilder
{
   protected ResteasyClient createResteasyClient(ClientHttpEngine engine,ExecutorService executor, boolean cleanupExecutor, ScheduledExecutorService scheduledExecutorService, ClientConfiguration config )
   {
      return new MPResteasyClient(engine, executor, cleanupExecutor, scheduledExecutorService, config);
   }
}
