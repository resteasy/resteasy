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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Cleaner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;

/**
 * A stream used for entities in a client. This may buffer, given the threshold value, in memory or be written to a file.
 * The file should be deleted once this stream is no longer referenced.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class EntityOutputStream extends OutputStream {
    private static final Cleaner CLEANER = Cleaner.create();

    private static class FileCleaner implements Runnable {
        private final Path path;

        private FileCleaner(final Path path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LogMessages.LOGGER.debugf(e, "Failed to delete file %s", path);
            }
        }
    }

    private final Object lock = new Object();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean exported = new AtomicBoolean();
    private final int threshold;
    private final Supplier<String> filePrefix;
    private final ByteArrayOutputStream inMemory;
    private final Path tmpDir;
    private volatile Path file;
    private volatile OutputStream delegate;

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param threshold  the maximum number of bytes to hold in memory
     * @param filePrefix the file prefix if a file is created
     */
    EntityOutputStream(final int threshold, final Supplier<String> filePrefix) {
        this(threshold, null, filePrefix);
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param threshold  the maximum number of bytes to hold in memory
     * @param tmpDir     the temporary directory used for files, can be {@code null} to use the default
     * @param filePrefix the file prefix if a file is created
     */
    EntityOutputStream(final int threshold, final Path tmpDir, final Supplier<String> filePrefix) {
        this.threshold = threshold;
        this.filePrefix = filePrefix;
        delegate = inMemory = new ByteArrayOutputStream(threshold);
        this.tmpDir = tmpDir;
    }

    @Override
    public void write(final int b) throws IOException {
        if (closed.get()) {
            throw new IllegalStateException(Messages.MESSAGES.streamIsClosed());
        }
        getDelegate(1).write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        if (closed.get()) {
            throw new IllegalStateException(Messages.MESSAGES.streamIsClosed());
        }
        getDelegate(b.length).write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (closed.get()) {
            throw new IllegalStateException(Messages.MESSAGES.streamIsClosed());
        }
        getDelegate(len).write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        getDelegate().flush();
    }

    @Override
    public void close() throws IOException {
        getDelegate().close();
        closed.set(true);
    }

    /**
     * Creates an input stream from this output stream.
     *
     * @return an input stream for reading the output
     *
     * @throws IOException if an error occurs creating the input stream
     */
    InputStream toInputStream() throws IOException {
        if (!closed.get()) {
            throw Messages.MESSAGES.streamNotClosed(this);
        }
        if (!exported.compareAndSet(false, true)) {
            throw Messages.MESSAGES.alreadyExported();
        }
        synchronized (lock) {
            if (file != null) {
                final InputStream in = Files.newInputStream(file);
                return new EntityInputStream(in, CLEANER.register(in, new FileCleaner(file)));
            }
            try {
                return new ByteArrayInputStream(inMemory.toByteArray());
            } finally {
                inMemory.reset();
            }
        }
    }

    /**
     * Creates an entity for the Apache HTTP Client based on the output stream.
     *
     * @return an entity for the output stream
     */
    AbstractHttpEntity toEntity() {
        if (!closed.get()) {
            throw Messages.MESSAGES.streamNotClosed(this);
        }
        if (!exported.compareAndSet(false, true)) {
            throw Messages.MESSAGES.alreadyExported();
        }
        synchronized (lock) {
            if (file != null) {
                final AbstractHttpEntity result = new FileEntity(file.toFile());
                CLEANER.register(result, new FileCleaner(file));
                return result;
            }
            try {
                return new ByteArrayEntity(inMemory.toByteArray());
            } finally {
                inMemory.reset();
            }
        }
    }

    /**
     * The length of the length of the content written.
     *
     * @return the length of the content written
     *
     * @throws IOException if there is an error determining the length of the content
     */
    long getContentLength() throws IOException {
        final Path file = this.file;
        return file == null ? inMemory.size() : Files.size(file);
    }

    private OutputStream getDelegate() {
        synchronized (lock) {
            return delegate;
        }
    }

    private OutputStream getDelegate(final int len) throws IOException {
        synchronized (lock) {
            if (file != null) {
                return delegate;
            }
            if ((len + inMemory.size()) < threshold) {
                return delegate;
            }
            if (tmpDir == null) {
                file = Files.createTempFile(filePrefix.get(), ".tmp");
            } else {
                file = Files.createTempFile(tmpDir, filePrefix.get(), ".tmp");
            }
            final OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE);
            inMemory.writeTo(out);
            inMemory.reset();
            delegate = out;
            return delegate;
        }
    }

    private static class EntityInputStream extends InputStream {
        private final InputStream delegate;
        private final Cleaner.Cleanable cleanable;

        private EntityInputStream(final InputStream delegate, final Cleaner.Cleanable cleanable) {
            this.delegate = delegate;
            this.cleanable = cleanable;
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(final int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
            cleanable.clean();
        }

        @Override
        public void mark(final int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public long transferTo(final OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }
    }
}