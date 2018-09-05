package org.jboss.resteasy.rxjava2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>, AsyncClientResponseProvider<Single<?>>
{
   private static class SingleAdaptor<T> extends CompletableFuture<T>
   {
      private Disposable subscription;

      SingleAdaptor(Single<T> single)
      {
         this.subscription = single.subscribe(this::complete, this::completeExceptionally);
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         subscription.dispose();
         return super.cancel(mayInterruptIfRunning);
      }
   }

   @Override
   public CompletionStage<?> toCompletionStage(Single<?> asyncResponse)
   {
      return new SingleAdaptor<>(asyncResponse);
   }

   @Override
   public Single<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return Single.fromFuture(completionStage.toCompletableFuture());
   }
}
