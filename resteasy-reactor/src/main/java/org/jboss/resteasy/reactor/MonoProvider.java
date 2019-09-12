package org.jboss.resteasy.reactor;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import reactor.core.publisher.Mono;

@Provider
public class MonoProvider implements AsyncResponseProvider<Mono<?>>, AsyncClientResponseProvider<Mono<?>>
{

   @Override
   public CompletionStage<?> toCompletionStage(Mono<?> asyncResponse)
   {
      return asyncResponse.toFuture();
   }

   @Override
   public Mono<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return Mono.fromFuture(completionStage.toCompletableFuture());
   }
}
