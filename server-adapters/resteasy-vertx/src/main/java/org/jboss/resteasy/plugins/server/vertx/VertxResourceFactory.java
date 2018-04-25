package org.jboss.resteasy.plugins.server.vertx;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VertxResourceFactory implements ResourceFactory
{

   private final ResourceFactory delegate;
   private final String id = UUID.randomUUID().toString();

   public VertxResourceFactory(ResourceFactory delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public Class<?> getScannableClass()
   {
      return delegate.getScannableClass();
   }

   @Override
   public void registered(ResteasyProviderFactory factory)
   {
      delegate.registered(factory);
   }

   @Override
   public CompletionStage<Object> createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory)
   {
      Context ctx = Vertx.currentContext();
      if (ctx != null)
      {
         Object resource = ctx.get(id);
         if (resource == null)
         {
            return delegate.createResource(request, response, factory).thenApply(newResource -> {
               ctx.put(id, newResource);
               return newResource;
            });
         }
         return CompletableFuture.completedFuture(resource);
      } else
      {
         throw new IllegalStateException();
      }
   }

   @Override
   public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
   {
      delegate.requestFinished(request, response, resource);
   }

   @Override
   public void unregistered()
   {
      delegate.unregistered();
   }
}
