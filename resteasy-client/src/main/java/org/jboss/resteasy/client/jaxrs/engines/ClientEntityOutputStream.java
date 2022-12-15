/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.EntityOutputStream;
import org.jboss.resteasy.spi.ResourceCleaner;
import org.jboss.resteasy.spi.config.Threshold;

/**
 * A stream used for entities in a client. This may buffer, given the threshold value, in memory or be written to a file.
 * The file should be deleted once this stream is no longer referenced.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ClientEntityOutputStream extends EntityOutputStream {

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param filePrefix the file prefix if a file is created
     */
    ClientEntityOutputStream(final Supplier<String> filePrefix) {
        super(filePrefix);
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param threshold  the maximum threshold of memory
     * @param tmpDir     the temporary directory used for files, can be {@code null} to use the default
     * @param filePrefix the file prefix if a file is created
     */
    ClientEntityOutputStream(final Threshold threshold, final Path tmpDir, final Supplier<String> filePrefix) {
        super(threshold, tmpDir, filePrefix);
    }

    HttpRequest.BodyPublisher toPublisher(final boolean wrapWithSize) throws IOException {
        if (!isClosed()) {
            throw Messages.MESSAGES.streamNotClosed(this);
        }
        checkExported(Messages.MESSAGES.alreadyExported());
        final HttpRequest.BodyPublisher delegate;
        final long len;
        synchronized (lock) {
            final Path file = getFile();
            final Supplier<InputStream> stream;
            if (file != null) {
                stream = () -> new CleanableFileInputStream(file);
                len = wrapWithSize ? Files.size(file) : -1;
            } else {
                final byte[] bytes = getAndClearMemory();
                len = wrapWithSize ? bytes.length : -1;
                stream = () -> new ByteArrayInputStream(bytes);
            }
            delegate = HttpRequest.BodyPublishers.ofInputStream(stream);
        }
        // The HttpRequest.BodyPublishers.fromPublisher(delegate, len) does not allow for -1 of the len, while the TCK
        // in some cases requires -1 to be returned. This is a simple workaround.
        return wrapWithSize ? new DelegateBodyPublisher(delegate, len) : delegate;
    }

    /**
     * Creates an entity for the Apache HTTP Client based on the output stream.
     *
     * @return an entity for the output stream
     */
    AbstractHttpEntity toEntity() {
        if (!isClosed()) {
            throw Messages.MESSAGES.streamNotClosed(this);
        }
        checkExported(Messages.MESSAGES.alreadyExported());
        synchronized (lock) {
            final Path file = getFile();
            if (file != null) {
                final AbstractHttpEntity result = new FileEntity(file.toFile());
                ResourceCleaner.register(result, new FileCleaner(file));
                return result;
            }
            return new ByteArrayEntity(getAndClearMemory());
        }
    }

    private static class DelegateBodyPublisher implements HttpRequest.BodyPublisher {
        private final HttpRequest.BodyPublisher delegate;
        private final long len;

        private DelegateBodyPublisher(final HttpRequest.BodyPublisher delegate, final long len) {
            this.delegate = delegate;
            this.len = len;
        }

        @Override
        public long contentLength() {
            return len;
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
            delegate.subscribe(subscriber);
        }
    }

    @SuppressWarnings("NullableProblems")
    private static class CleanableFileInputStream extends InputStream {
        private final Path file;
        private final AtomicBoolean closed;
        private final Lock lock;
        private volatile Cleaner.Cleanable cleanable;
        private volatile InputStream delegate;

        private CleanableFileInputStream(final Path file) {
            this.file = file;
            closed = new AtomicBoolean(false);
            lock = new ReentrantLock();
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            return getDelegate().read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            checkClosed();
            return getDelegate().read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            checkClosed();
            return getDelegate().read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            checkClosed();
            return getDelegate().readAllBytes();
        }

        @Override
        public byte[] readNBytes(final int len) throws IOException {
            checkClosed();
            return getDelegate().readNBytes(len);
        }

        @Override
        public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
            checkClosed();
            return getDelegate().readNBytes(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            checkClosed();
            return getDelegate().skip(n);
        }

        @Override
        public int available() throws IOException {
            checkClosed();
            return getDelegate().available();
        }

        @Override
        public void close() throws IOException {
            if (closed.compareAndSet(false, true)) {
                lock.lock();
                try {
                    try {
                        if (delegate != null) {
                            delegate.close();
                        }
                    } finally {
                        if (cleanable != null) {
                            cleanable.clean();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        @Override
        public void mark(final int readlimit) {
            if (!closed.get()) {
                getDelegate().mark(readlimit);
            }
        }

        @Override
        public void reset() throws IOException {
            checkClosed();
            getDelegate().reset();
        }

        @Override
        public boolean markSupported() {
            return !closed.get() && getDelegate().markSupported();
        }

        @Override
        public long transferTo(final OutputStream out) throws IOException {
            checkClosed();
            return getDelegate().transferTo(out);
        }

        private void checkClosed() throws IOException {
            if (closed.get()) {
                throw new IOException(Messages.MESSAGES.streamIsClosed());
            }
        }

        private InputStream getDelegate() {
            lock.lock();
            try {
                if (delegate == null) {
                    if (Files.notExists(file)) {
                        throw Messages.MESSAGES.noContentFound();
                    }
                    try {
                        delegate = Files.newInputStream(file);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    cleanable = ResourceCleaner.register(delegate, new FileCleaner(file));
                }
                return delegate;
            } finally {
                lock.unlock();
            }
        }
    }
}