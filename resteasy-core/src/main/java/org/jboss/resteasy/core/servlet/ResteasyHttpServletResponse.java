/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.servlet;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Wraps an {@linkplain HttpServletResponse} to know when the output stream has been written to.
 * <p>
 * An instance holds the output stream of a single response, so it must only be used for one request.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ResteasyHttpServletResponse extends HttpServletResponseWrapper {
    private final Lock lock = new ReentrantLock();

    private WriteTrackingServletOutputStream servletOutputStream;
    private WriteTrackingPrintWriter writer;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public ResteasyHttpServletResponse(final HttpServletResponse response) {
        super(response);
    }

    /**
     * Creates a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public static ResteasyHttpServletResponse of(final HttpServletResponse response) {
        return new ResteasyHttpServletResponse(response);
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            super.reset();
            servletOutputStream = null;
            writer = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WriteTrackingServletOutputStream getOutputStream() throws IOException {
        lock.lock();
        try {
            if (servletOutputStream == null) {
                servletOutputStream = new WriteTrackingServletOutputStream(super.getOutputStream());
            }
            return servletOutputStream;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WriteTrackingPrintWriter getWriter() throws IOException {
        lock.lock();
        try {
            if (writer == null) {
                writer = new WriteTrackingPrintWriter(super.getWriter());
            }
            return writer;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the {@linkplain #getOutputStream() output stream} or {@linkplain #getWriter() writer} has been written
     * to. Neither is obtained if it has not been yet.
     *
     * @return {@code true} if the output stream or writer has been written to, otherwise {@code false}
     */
    public boolean isWritten() {
        lock.lock();
        try {
            return (servletOutputStream != null && servletOutputStream.isWritten()) ||
                    (writer != null && writer.isWritten());
        } finally {
            lock.unlock();
        }
    }
}
