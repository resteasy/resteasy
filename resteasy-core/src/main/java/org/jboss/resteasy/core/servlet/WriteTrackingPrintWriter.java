/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.servlet;

import java.io.PrintWriter;
import java.util.Locale;

/**
 * A {@link PrintWriter} that tracks whether any data has been written to the writer.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class WriteTrackingPrintWriter extends PrintWriter {
    private final PrintWriter delegate;
    private volatile boolean written;

    public WriteTrackingPrintWriter(PrintWriter delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean checkError() {
        return delegate.checkError();
    }

    @Override
    public void write(final int c) {
        markWritten();
        delegate.write(c);
    }

    @Override
    public void write(final char[] buf, final int off, final int len) {
        markWritten();
        delegate.write(buf, off, len);
    }

    @Override
    public void write(final char[] buf) {
        markWritten();
        delegate.write(buf);
    }

    @Override
    public void write(final String s, final int off, final int len) {
        markWritten();
        delegate.write(s, off, len);
    }

    @Override
    public void write(final String s) {
        markWritten();
        delegate.write(s);
    }

    @Override
    public void print(final boolean b) {
        markWritten();
        delegate.print(b);
    }

    @Override
    public void print(final char c) {
        markWritten();
        delegate.print(c);
    }

    @Override
    public void print(final int i) {
        markWritten();
        delegate.print(i);
    }

    @Override
    public void print(final long l) {
        markWritten();
        delegate.print(l);
    }

    @Override
    public void print(final float f) {
        markWritten();
        delegate.print(f);
    }

    @Override
    public void print(final double d) {
        markWritten();
        delegate.print(d);
    }

    @Override
    public void print(final char[] s) {
        markWritten();
        delegate.print(s);
    }

    @Override
    public void print(final String s) {
        markWritten();
        delegate.print(s);
    }

    @Override
    public void print(final Object obj) {
        markWritten();
        delegate.print(obj);
    }

    @Override
    public void println() {
        markWritten();
        delegate.println();
    }

    @Override
    public void println(final boolean x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final char x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final int x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final long x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final float x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final double x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final char[] x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final String x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public void println(final Object x) {
        markWritten();
        delegate.println(x);
    }

    @Override
    public PrintWriter printf(final String format, final Object... args) {
        markWritten();
        return delegate.printf(format, args);
    }

    @Override
    public PrintWriter printf(final Locale l, final String format, final Object... args) {
        markWritten();
        return delegate.printf(l, format, args);
    }

    @Override
    public PrintWriter format(final String format, final Object... args) {
        markWritten();
        return delegate.format(format, args);
    }

    @Override
    public PrintWriter format(final Locale l, final String format, final Object... args) {
        markWritten();
        return delegate.format(l, format, args);
    }

    @Override
    public PrintWriter append(final CharSequence csq) {
        markWritten();
        return delegate.append(csq);
    }

    @Override
    public PrintWriter append(final CharSequence csq, final int start, final int end) {
        markWritten();
        return delegate.append(csq, start, end);
    }

    @Override
    public PrintWriter append(final char c) {
        markWritten();
        return delegate.append(c);
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
