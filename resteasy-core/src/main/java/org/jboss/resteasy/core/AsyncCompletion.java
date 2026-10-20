/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

/**
 * Tracks whether an asynchronous response has gone back to the container. After that, the container can recycle the
 * request and the response and give them to the next request on the connection, so RESTEasy must not use either of
 * them, and an exception that arrives later must be logged, not written to the response.
 */
final class AsyncCompletion {
    private final BooleanSupplier handedBack;
    private final boolean callbackReports;
    private volatile boolean written;
    private volatile Throwable writeFailure;

    private AsyncCompletion(final BooleanSupplier handedBack, final boolean callbackReports) {
        this.handedBack = handedBack;
        this.callbackReports = callbackReports;
    }

    /**
     * @return a tracker for a response that goes back to the container when its writer callback runs, where the
     *         callback reports the write failure it receives
     */
    static AsyncCompletion whenWritten() {
        return new AsyncCompletion(null, true);
    }

    /**
     * @param handedBack tells whether the response went back to the container
     * @return a tracker for a response whose writer callback can ignore the write failure it receives
     */
    static AsyncCompletion whenComplete(final BooleanSupplier handedBack) {
        return new AsyncCompletion(Objects.requireNonNull(handedBack), false);
    }

    /**
     * @param handedBack tells whether the response went back to the container
     * @return a tracker for a response whose writer callback reports the write failure it receives
     */
    static AsyncCompletion whenCompleteReportedByCallback(final BooleanSupplier handedBack) {
        return new AsyncCompletion(Objects.requireNonNull(handedBack), true);
    }

    /**
     * @param callback the writer callback
     * @return a writer callback that records the write result before it runs {@code callback}
     */
    Consumer<Throwable> onWritten(final Consumer<Throwable> callback) {
        return t -> {
            writeFailure = t;
            written = true;
            callback.accept(t);
        };
    }

    /**
     * Logs an exception that arrived after the response went back to the container.
     *
     * @param t the exception
     * @return {@code true} if the response went back to the container, and the caller must not use the request or the
     *         response
     */
    boolean afterCompletion(final Throwable t) {
        if (!(handedBack == null ? written : handedBack.getAsBoolean()))
            return false;
        if (reported(t))
            LogMessages.LOGGER.debug("Exception after the asynchronous response completed", t);
        else
            LogMessages.LOGGER.exceptionAfterAsynchronousCompletion(t);
        return true;
    }

    /**
     * @param t the exception
     * @return {@code true} if the writer callback already reported {@code t} or its cause
     */
    private boolean reported(final Throwable t) {
        Throwable cause = writeFailure;
        if (!callbackReports || cause == null)
            return false;
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable c = t; c != null && seen.add(c); c = c.getCause()) {
            if (c == cause)
                return true;
        }
        return false;
    }
}
