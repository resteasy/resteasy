package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

public interface AsyncResponseProvider<T> {
   public CompletionStage toCompletionStage(T asyncResponse);
}
