/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi;

/**
 * A listener notified of events in the processing of a request. This is intended for an integration, not for an
 * application. Implementations are discovered with a {@link java.util.ServiceLoader} and are invoked in ascending
 * {@link jakarta.annotation.Priority} order, the lowest value first, with implementations of equal priority invoked
 * in the order they were discovered.
 * <p>
 * A listener is invoked on the thread which completed the request, which may be a container I/O thread, and the
 * contextual data of the request may not be available. The invoking thread is held until the listener returns, so an
 * implementation should only block where the integration knows the thread tolerates it.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @since 8.0
 */
public interface RequestListener {

    /**
     * Invoked once the processing of the request has completed. A request has completed once the dispatch of the
     * request has returned and, if the request was suspended, the asynchronous response has completed. For example
     * after it was resumed, cancelled, timed out or the client connection was closed. This is invoked exactly once per
     * request.
     *
     * @param request  the request which has completed
     * @param response the response for the request
     */
    void requestCompleted(HttpRequest request, HttpResponse response);
}
