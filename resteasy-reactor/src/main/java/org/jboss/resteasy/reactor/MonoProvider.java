package org.jboss.resteasy.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Provider
public class MonoProvider implements AsyncResponseProvider<Mono<?>>, AsyncClientResponseProvider<Mono<?>>
{
   private static class MonoAdaptor<T> extends CompletableFuture<T>
   {
      private Disposable subscription;

      MonoAdaptor(final Mono<T> single)
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
   public CompletionStage<?> toCompletionStage(Mono<?> asyncResponse)
   {
      return new MonoAdaptor<>(asyncResponse);
   }

   @Override
   public Mono<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return Mono.fromFuture(completionStage.toCompletableFuture());
   }
}
