/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi;

/**
 * A {@link ValueInjector} for streams to indicate if content is available in the injected value.
 * <p>
 * This is useful for wrapping {@linkplain java.io.InputStream input} or {@linkplain java.io.OutputStream output} streams
 * which may contain content.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @since 8.0
 */
public interface StreamValueInjector extends ValueInjector {

    /**
     * Checks if there is content for the stream value injected for the request available.
     *
     * @param request the current request
     *
     * @return {@code true} if there is content available, otherwise {@code false}
     */
    boolean isContentAvailable(HttpRequest request);
}
