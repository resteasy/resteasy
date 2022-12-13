package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * Used to turn a CompletionStage into another reactive class.
 * Can be used for implementing RxInvokers for other suitable classes.
 */
public interface AsyncClientResponseProvider<T> {

    /**
     * Turns {@link CompletionStage} to a reactive type.
     *
     * @param completionStage The {@link CompletionStage} that will produce a value.
     *
     * @return T reactive type
     */
    T fromCompletionStage(CompletionStage<?> completionStage);

    /**
     * Turns {@link CompletionStage} to a reactive type in a deferred fashion. For instance, in the case of
     * Rx or Reactor, the {@link Supplier#get()} will only be called when subscription happens.
     *
     * @param completionStageSupplier The {@link Supplier} of a {@link CompletionStage} that will produce a value.
     *                                This allows lazy triggering of future-based APIs.
     *
     * @return T reactive type
     */
    T fromCompletionStage(Supplier<CompletionStage<?>> completionStageSupplier);
}
