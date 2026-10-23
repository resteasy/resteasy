/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core;

import java.util.List;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.RequestListener;

/**
 * Notifies the {@link RequestListener}s exactly once when the processing of a request has completed. A
 * request has completed once its dispatch has returned and, if it was suspended, the asynchronous response has completed.
 * The asynchronous response may complete before the dispatch returns, e.g. when the resource method resumes it on the
 * invoking thread, or after, on any thread.
 * <p>
 * The state is only created when listeners are registered, and is created at the start of the dispatch on the thread
 * processing the request. The other completion paths only read it.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class RequestCompletion {
    private static final String ATTRIBUTE_NAME = RequestCompletion.class.getName();

    private final List<RequestListener> listeners;
    private int dispatchDepth;
    private boolean dispatchEnded;
    private boolean suspended;
    private boolean responseCompleted;
    private boolean completed;

    private RequestCompletion(final List<RequestListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Marks the start of a dispatch of the request. Nested dispatches of the same request are counted, so the request
     * is not completed before the outermost dispatch returns.
     *
     * @param request   the request being dispatched
     * @param listeners the listeners to notify, if empty nothing is tracked
     */
    static void dispatchStarted(final HttpRequest request, final List<RequestListener> listeners) {
        if (listeners.isEmpty()) {
            return;
        }
        RequestCompletion completion = (RequestCompletion) request.getAttribute(ATTRIBUTE_NAME);
        if (completion == null) {
            completion = new RequestCompletion(listeners);
            request.setAttribute(ATTRIBUTE_NAME, completion);
        }
        completion.incrementDispatchDepth();
    }

    /**
     * Marks the end of a dispatch of the request. If this is the outermost dispatch and the request is not suspended, or
     * the asynchronous response has already completed, the listeners are notified.
     *
     * @param request  the request being dispatched
     * @param response the response for the request
     */
    static void dispatchEnded(final HttpRequest request, final HttpResponse response) {
        final RequestCompletion completion = (RequestCompletion) request.getAttribute(ATTRIBUTE_NAME);
        if (completion != null && completion.markDispatchEnded(request.getAsyncContext().isSuspended())) {
            completion.notifyListeners(request, response);
        }
    }

    /**
     * Marks the start of a dispatch which resumes the processing of a suspended request after the initial dispatch has
     * returned, e.g. invoking the resource method once its arguments were resolved asynchronously. The request does not
     * complete until the matching {@link #dispatchEnded(HttpRequest, HttpResponse)}.
     *
     * @param request the request being dispatched
     *
     * @return {@code true} if the dispatch may proceed or {@code false} if the request has already completed
     */
    static boolean dispatchResumed(final HttpRequest request) {
        final RequestCompletion completion = (RequestCompletion) request.getAttribute(ATTRIBUTE_NAME);
        return completion == null || completion.resumeDispatch();
    }

    /**
     * Marks the asynchronous response as completed. If the dispatch has already returned, the listeners are notified.
     * Otherwise, they are notified when the dispatch returns.
     *
     * @param request  the request
     * @param response the response for the request
     */
    static void responseCompleted(final HttpRequest request, final HttpResponse response) {
        final RequestCompletion completion = (RequestCompletion) request.getAttribute(ATTRIBUTE_NAME);
        if (completion != null && completion.markResponseCompleted()) {
            completion.notifyListeners(request, response);
        }
    }

    private synchronized void incrementDispatchDepth() {
        dispatchDepth++;
    }

    private synchronized boolean markDispatchEnded(final boolean suspended) {
        if (--dispatchDepth > 0) {
            return false;
        }
        dispatchEnded = true;
        this.suspended = suspended;
        return tryComplete();
    }

    private synchronized boolean resumeDispatch() {
        if (completed) {
            return false;
        }
        dispatchDepth++;
        return true;
    }

    private synchronized boolean markResponseCompleted() {
        responseCompleted = true;
        return tryComplete();
    }

    // Must be invoked while holding the lock
    private boolean tryComplete() {
        if (completed || dispatchDepth > 0 || !dispatchEnded || (suspended && !responseCompleted)) {
            return false;
        }
        completed = true;
        return true;
    }

    private void notifyListeners(final HttpRequest request, final HttpResponse response) {
        for (RequestListener listener : listeners) {
            try {
                listener.requestCompleted(request, response);
            } catch (RuntimeException e) {
                LogMessages.LOGGER.failedToNotifyRequestListener(e, listener.getClass().getName());
            }
        }
    }
}
