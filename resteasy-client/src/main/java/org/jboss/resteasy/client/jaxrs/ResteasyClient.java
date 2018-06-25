package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyClient implements Client
{
   protected volatile ClientHttpEngine httpEngine;
   protected volatile ExecutorService asyncInvocationExecutor;
   protected volatile ScheduledExecutorService scheduledExecutorService;
   protected ClientConfiguration configuration;
   protected boolean closed;
   protected boolean cleanupExecutor;


   protected ResteasyClient(ClientHttpEngine httpEngine, ExecutorService asyncInvocationExecutor, boolean cleanupExecutor,
         ScheduledExecutorService scheduledExecutorService, ClientConfiguration configuration)
   {
      this.cleanupExecutor = cleanupExecutor;
      this.httpEngine = httpEngine;
      this.asyncInvocationExecutor = asyncInvocationExecutor;
      this.configuration = configuration;
      this.scheduledExecutorService = scheduledExecutorService;
   }

   protected ResteasyClient(ClientHttpEngine httpEngine, ExecutorService asyncInvocationExecutor, boolean cleanupExecutor, ClientConfiguration configuration)
   {
      this.cleanupExecutor = cleanupExecutor;
      this.httpEngine = httpEngine;
      this.asyncInvocationExecutor = asyncInvocationExecutor;
      this.configuration = configuration;
   }

   public ClientHttpEngine httpEngine()
   {
      abortIfClosed();
      return httpEngine;
   }


   public ExecutorService asyncInvocationExecutor()
   {
      return asyncInvocationExecutor;
   }

   public ScheduledExecutorService getScheduledExecutor()
   {
      return this.scheduledExecutorService;
   }

   public void abortIfClosed()
   {
      if (isClosed()) throw new IllegalStateException(Messages.MESSAGES.clientIsClosed());
   }

   public boolean isClosed()
   {
      return closed;
   }

   @Override
   public void close()
   {
      closed = true;
      try
      {
         httpEngine.close();
         if (cleanupExecutor)
         {
            if (System.getSecurityManager() == null)
            {
               asyncInvocationExecutor.shutdown();
            }
            else
            {
               AccessController.doPrivileged(new PrivilegedAction<Void>()
               {
                  @Override
                  public Void run()
                  {
                     asyncInvocationExecutor.shutdown();
                     return null;
                  }
               });
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Configuration getConfiguration()
   {
      abortIfClosed();
      return configuration;
   }

   @Override
   public SSLContext getSslContext()
   {
      abortIfClosed();
      return httpEngine().getSslContext();
   }

   @Override
   public HostnameVerifier getHostnameVerifier()
   {
      abortIfClosed();
      return httpEngine().getHostnameVerifier();
   }

   @Override
   public ResteasyClient property(String name, Object value)
   {
      abortIfClosed();
      configuration.property(name, value);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass)
   {
      abortIfClosed();
      configuration.register(componentClass);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, int priority)
   {
      abortIfClosed();
      configuration.register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, Class<?>... contracts)
   {
      abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Object component)
   {
      abortIfClosed();
      configuration.register(component);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, int priority)
   {
      abortIfClosed();
      configuration.register(component, priority);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, Class<?>... contracts)
   {
      abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, Map<Class<?>, Integer> contracts)
   {
      abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget target(String uri) throws IllegalArgumentException, NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException(Messages.MESSAGES.uriWasNull());
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(URI uri) throws NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException(Messages.MESSAGES.uriWasNull());
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(UriBuilder uriBuilder) throws NullPointerException
   {
      abortIfClosed();
      if (uriBuilder == null) throw new NullPointerException(Messages.MESSAGES.uriBuilderWasNull());
      return new ClientWebTarget(this, uriBuilder, configuration);
   }

   @Override
   public ResteasyWebTarget target(Link link) throws NullPointerException
   {
      abortIfClosed();
      if (link == null) throw new NullPointerException(Messages.MESSAGES.linkWasNull());
      URI uri = link.getUri();
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public Invocation.Builder invocation(Link link) throws NullPointerException, IllegalArgumentException
   {
      abortIfClosed();
      if (link == null) throw new NullPointerException(Messages.MESSAGES.linkWasNull());
      WebTarget target = target(link);
      if (link.getType() != null) return target.request(link.getType());
      else return target.request();
   }

}
