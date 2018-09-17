package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

public interface AsyncResponseProvider<T> {
   CompletionStage toCompletionStage(T asyncResponse);
}
