package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

public interface AsyncClientResponseProvider<T> {

   public T fromCompletionStage(CompletionStage<?> completionStage);
}
