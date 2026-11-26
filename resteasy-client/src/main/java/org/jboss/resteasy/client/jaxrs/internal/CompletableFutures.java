/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

/**
 * Utilities for {@link CompletableFuture} instances used by the client.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public final class CompletableFutures {

    private CompletableFutures() {
    }

    /**
     * Cancels the source when the dependent is cancelled. Cancelling a {@link CompletableFuture} does not cancel the
     * stages it depends on, so the work of the source continues unless it is cancelled explicitly.
     * <p>
     * If the dependent is already cancelled, the source is cancelled immediately.
     * </p>
     *
     * @param dependent the future returned to the caller, cannot be {@code null}
     * @param source    the future whose work is cancelled with the dependent, cannot be {@code null}
     * @param <T>       the result type of the dependent
     *
     * @return the {@code dependent} future itself, not a new stage
     */
    public static <T> CompletableFuture<T> cancelWith(final CompletableFuture<T> dependent, final Future<?> source) {
        dependent.whenComplete((value, error) -> {
            if (dependent.isCancelled()) {
                source.cancel(true);
            }
        });
        return dependent;
    }

    /**
     * Returns the cause of a {@link CompletionException}, which a {@link CompletableFuture} stage adds around an
     * exception thrown by its function.
     *
     * @param error the exception a stage completed with, can be {@code null}
     *
     * @return the cause if {@code error} is a {@link CompletionException} with a cause, otherwise {@code error}
     */
    public static Throwable unwrap(final Throwable error) {
        if (error instanceof CompletionException && error.getCause() != null) {
            return error.getCause();
        }
        return error;
    }
}
