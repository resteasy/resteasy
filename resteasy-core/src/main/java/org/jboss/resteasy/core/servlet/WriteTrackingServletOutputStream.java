/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * A {@link ServletOutputStream} which tracks whether any data has been written to the stream.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class WriteTrackingServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream delegate;
    private volatile boolean written;

    /**
     * Creates a new tracking output stream
     *
     * @param delegate the delegate
     */
    WriteTrackingServletOutputStream(final ServletOutputStream delegate) {
        this.delegate = delegate;
        this.written = false;
    }

    @Override
    public void write(final ByteBuffer buffer) throws IOException {
        delegate.write(buffer);
        markWritten();
    }

    @Override
    public void print(final String s) throws IOException {
        delegate.print(s);
        markWritten();
    }

    @Override
    public void print(final boolean b) throws IOException {
        delegate.print(b);
        markWritten();
    }

    @Override
    public void print(final char c) throws IOException {
        delegate.print(c);
        markWritten();
    }

    @Override
    public void print(final int i) throws IOException {
        delegate.print(i);
        markWritten();
    }

    @Override
    public void print(final long l) throws IOException {
        delegate.print(l);
        markWritten();
    }

    @Override
    public void print(final float f) throws IOException {
        delegate.print(f);
        markWritten();
    }

    @Override
    public void print(final double d) throws IOException {
        delegate.print(d);
        markWritten();
    }

    @Override
    public void println() throws IOException {
        delegate.println();
        markWritten();
    }

    @Override
    public void println(final String s) throws IOException {
        delegate.println(s);
        markWritten();
    }

    @Override
    public void println(final boolean b) throws IOException {
        delegate.println(b);
        markWritten();
    }

    @Override
    public void println(final char c) throws IOException {
        delegate.println(c);
        markWritten();
    }

    @Override
    public void println(final int i) throws IOException {
        delegate.println(i);
        markWritten();
    }

    @Override
    public void println(final long l) throws IOException {
        delegate.println(l);
        markWritten();
    }

    @Override
    public void println(final float f) throws IOException {
        delegate.println(f);
        markWritten();
    }

    @Override
    public void println(final double d) throws IOException {
        delegate.println(d);
        markWritten();
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setWriteListener(final WriteListener writeListener) {
        delegate.setWriteListener(writeListener);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void write(final int b) throws IOException {
        delegate.write(b);
        markWritten();
    }

    @Override
    public void write(final byte[] b) throws IOException {
        delegate.write(b);
        markWritten();
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        delegate.write(b, off, len);
        markWritten();
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    /**
     * Indicates whether the stream has been written to.
     *
     * @return {@code true} if a write has occurred, otherwise {@code false}
     */
    public boolean isWritten() {
        return written;
    }

    private void markWritten() {
        if (!written) {
            written = true;
        }
    }
}
