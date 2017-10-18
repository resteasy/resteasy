package org.jboss.resteasy.rxjava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncResponseProvider;

import rx.Single;
import rx.Subscription;
import rx.plugins.RxJavaHooks;

@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>
{

   static
   {
      RxJavaHooks.setOnSingleCreate(new ResteasyContextPropagatingOnSingleCreateAction());
   }

   private static class SingleAdaptor<T> extends CompletableFuture<T>
   {
      private Subscription subscription;

      public SingleAdaptor(Single<T> observable)
      {
         this.subscription = observable.subscribe(this::complete, this::completeExceptionally);
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         subscription.unsubscribe();
         return super.cancel(mayInterruptIfRunning);
      }
   }

   @Override
   public CompletionStage<?> toCompletionStage(Single<?> asyncResponse)
   {
      return new SingleAdaptor<>(asyncResponse);
   }

}
