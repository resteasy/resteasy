package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

/**
 * Used to turn a CompletionStage into another reactive class.
 * Can be used for implementing RxInvokers for other suitable classes.
 */
public interface AsyncClientResponseProvider<T> {

   T fromCompletionStage(CompletionStage<?> completionStage);
}
