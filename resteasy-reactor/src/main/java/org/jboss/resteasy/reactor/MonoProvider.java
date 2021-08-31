package org.jboss.resteasy.reactor;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import reactor.core.publisher.Mono;

@Provider
public class MonoProvider implements AsyncResponseProvider<Mono<?>>, AsyncClientResponseProvider<Mono<?>>
{

   @Override
   public CompletionStage<?> toCompletionStage(final Mono<?> asyncResponse)
   {
      return asyncResponse.toFuture();
   }

   @Override
   public Mono<?> fromCompletionStage(final CompletionStage<?> completionStage)
   {
      return Mono.fromFuture(completionStage.toCompletableFuture());
   }

   @Override
   public Mono<?> fromCompletionStage(final Supplier<CompletionStage<?>> completionStageSupplier)
   {
      return Mono.fromFuture(() -> completionStageSupplier.get().toCompletableFuture());
   }
}
