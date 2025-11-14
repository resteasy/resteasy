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

package org.jboss.resteasy.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.jboss.logging.Logger;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.config.SizeUnit;
import org.jboss.resteasy.spi.config.Threshold;

/**
 * A stream used for entities. This may buffer, given the threshold value, in memory or be written to a file. The file
 * should be deleted after the {@link #toInputStream() resulting input stream} is no longer referenced.
 * <p>
 * Please note that if {@link #toInputStream()} is not invoked the file <strong>must</strong> be manually deleted.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see 6.1
 */
@SuppressWarnings("unused")
public class EntityOutputStream extends OutputStream {
    private static final Logger LOGGER = Logger.getLogger(EntityOutputStream.class);
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final int BUFFER_SIZE = getOptionValue(Options.ENTITY_FILE_BUFFER_SIZE);

    protected final Object lock = new Object();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean exported = new AtomicBoolean();
    private final Threshold memoryThreshold;
    private final Threshold fileThreshold;
    private final Supplier<String> filePrefix;
    private final ByteArrayOutputStream inMemory;
    private final Path tmpDir;
    private volatile Path file;
    private volatile OutputStream delegate;
    private long bytesWritten;

    /**
     * Creates a new entity stream with the maximum in memory threshold of a default value.
     */
    public EntityOutputStream() {
        this(getOptionValue(Options.ENTITY_MEMORY_THRESHOLD), getTempDir(), () -> "resteasy-entity");
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold of the supplied value.
     *
     * @param memoryThreshold the maximum number of bytes to hold in memory
     */
    public EntityOutputStream(final Threshold memoryThreshold) {
        this(memoryThreshold, getTempDir(), () -> "resteasy-entity");
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param memoryThreshold the maximum number of bytes to hold in memory
     * @param filePrefix      the file prefix if a file is created
     */
    public EntityOutputStream(final Threshold memoryThreshold, final Supplier<String> filePrefix) {
        this(memoryThreshold, getTempDir(), filePrefix);
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param memoryThreshold the maximum number of bytes to hold in memory
     * @param tmpDir          the temporary directory used for files, can be {@code null} to use the default
     * @param filePrefix      the file prefix if a file is created
     */
    public EntityOutputStream(final Threshold memoryThreshold, final Path tmpDir, final Supplier<String> filePrefix) {
        this(memoryThreshold, tmpDir, getOptionValue(Options.ENTITY_FILE_THRESHOLD), filePrefix);
    }

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param memoryThreshold the maximum number of bytes to hold in memory
     * @param tmpDir          the temporary directory used for files, can be {@code null} to use the default
     * @param filePrefix      the file prefix if a file is created
     */
    public EntityOutputStream(final Threshold memoryThreshold, final Path tmpDir, final Threshold fileThreshold,
            final Supplier<String> filePrefix) {
        this.memoryThreshold = memoryThreshold;
        this.filePrefix = filePrefix;
        this.fileThreshold = fileThreshold;
        // Check that we can hold this much memory. This is the best guess, but we're only logging a debug message.
        if (LOGGER.isDebugEnabled()) {
            final Runtime runtime = Runtime.getRuntime();
            final long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            final long maxMemory = runtime.maxMemory();
            if ((usedMemory + memoryThreshold.toBytes()) >= maxMemory) {
                LOGGER.debugf(new Throwable("StackTrace for debugging purposes."),
                        "The JVM may run out of memory allocating the memory buffer. " +
                                "The available size is %s with %s used while attempting to allocate %s.",
                        SizeUnit.toHumanReadable(maxMemory), SizeUnit.toHumanReadable(usedMemory),
                        SizeUnit.toHumanReadable(memoryThreshold.toBytes()));
            }
        }
        delegate = inMemory = new ByteArrayOutputStream();
        this.tmpDir = tmpDir;
        bytesWritten = 0;
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
        synchronized (lock) {
            delegate.flush();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            synchronized (lock) {
                delegate.close();
            }
        } finally {
            closed.set(true);
        }
    }

    /**
     * Checks if the output stream has been closed.
     *
     * @return {@code true} if the output stream has been closed, otherwise {@code false}
     */
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Creates an input stream from this output stream.
     * <p>
     * Note that when invoking this method {@link #close()} is invoked first.
     * </p>
     *
     * @return an input stream for reading the output
     *
     * @throws IOException if an error occurs creating the input stream
     */
    public InputStream toInputStream() throws IOException {
        checkExported(Messages.MESSAGES.alreadyExported());
        synchronized (lock) {
            close();
            final Path file = getFile();
            if (file != null) {
                return new EntityInputStream(file);
            }
            return new ByteArrayInputStream(getAndClearMemory());
        }
    }

    /**
     * Returns the file if one was written to. If this is in memory, this method will return {@code null}.
     * <p>
     * <strong>Important:</strong> You <strong>must</strong> use the {@link #lock} when accessing this method.
     * </p>
     *
     * @return the file, if it exists, or {@code null}
     */
    protected Path getFile() {
        return file;
    }

    /**
     * Returns the data in memory if {@link #getFile()} returns {@code null}.
     * <p>
     * <strong>Important:</strong> You <strong>must</strong> use the {@link #lock} when accessing this method.
     * </p>
     *
     * @return the data in memory or an empty array
     */
    protected byte[] getAndClearMemory() {
        if (file != null) {
            return EMPTY_BYTES;
        }
        try {
            return inMemory.toByteArray();
        } finally {
            inMemory.reset();
        }
    }

    protected void checkExported(final Supplier<? extends RuntimeException> errorMessage) {
        if (!exported.compareAndSet(false, true)) {
            throw errorMessage.get();
        }
    }

    /**
     * The length of the length of the content written.
     *
     * @return the length of the content written
     *
     * @throws IOException if there is an error determining the length of the content
     */
    public long getContentLength() throws IOException {
        synchronized (lock) {
            return file == null ? inMemory.size() : Files.size(file);
        }
    }

    private OutputStream getDelegate(final int len) throws IOException {
        synchronized (lock) {
            if (file != null) {
                checkFileThreshold(len);
                return delegate;
            }
            if (!memoryThreshold.reached(len + inMemory.size())) {
                return delegate;
            }
            if (tmpDir == null) {
                file = Files.createTempFile(filePrefix.get(), ".tmp");
            } else {
                file = Files.createTempFile(tmpDir, filePrefix.get(), ".tmp");
            }
            bytesWritten = inMemory.size();
            checkFileThreshold(len);
            final OutputStream out = BUFFER_SIZE > 0 ? new ChunkedOutputStream(file) : Files.newOutputStream(file);
            try {
                inMemory.writeTo(out);
                inMemory.reset();
                delegate = out;
            } catch (IOException e) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
                final var file = this.file;
                this.file = null;
                try {
                    Files.delete(file);
                } catch (IOException deleteException) {
                    LOGGER.debugf(deleteException, "Failed to delete temporary file %s", file);
                }
                throw e;
            }
            return delegate;
        }
    }

    private void checkFileThreshold(final int len) {
        synchronized (lock) {
            if (file != null) {
                bytesWritten += len;
                if (fileThreshold.reached(bytesWritten)) {
                    // Close the output stream and delete the file
                    try {
                        close();
                    } catch (IOException e) {
                        LOGGER.tracef(e, "Failed to close input stream %s", this);
                    } finally {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            LOGGER.tracef(e, "Failed to delete file %s", file);
                        }
                    }
                    throw Messages.MESSAGES.fileLimitReached(fileThreshold, Options.ENTITY_FILE_THRESHOLD.name());
                }
            }
        }
    }

    private static Path getTempDir() {
        return getOptionValue(Options.ENTITY_TMP_DIR);
    }

    private static <T> T getOptionValue(final Options<T> option) {
        if (System.getSecurityManager() == null) {
            return option.getValue();
        }
        return AccessController.doPrivileged((PrivilegedAction<T>) option::getValue);
    }

    /**
     * This serves as a wrapper to limit the peak amount that will be written at once
     * through to the java nio File. This is to limit the peak DirectByteBuffer the nio
     * layer will allocate since it would allocate a thread local buffer matching in size
     * to the size of the largest write call to the file.
     */
    private static class ChunkedOutputStream extends OutputStream {
        private final OutputStream delegate;
        private final Path file;

        private ChunkedOutputStream(final Path file) throws IOException {
            this.delegate = Files.newOutputStream(file);
            this.file = file;
        }

        @Override
        public void write(int b) throws IOException {
            try {
                delegate.write(b);
            } catch (IOException e) {
                deleteFile();
                throw e;
            }
        }

        @Override
        public void write(final byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            try {
                int pos = off;
                while (pos < off + len) {
                    int writeAmount = Math.min(BUFFER_SIZE, off + len - pos);
                    delegate.write(b, pos, writeAmount);
                    pos += writeAmount;
                }
            } catch (IOException e) {
                deleteFile();
                throw e;
            }
        }

        @Override
        public void close() throws IOException {
            try {
                delegate.close();
            } catch (IOException e) {
                deleteFile();
                throw e;
            }
        }

        private void deleteFile() {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignore) {
            }
        }
    }

    protected static class EntityInputStream extends InputStream {
        private final InputStream delegate;

        public EntityInputStream(final Path file) {
            try {
                this.delegate = Files.newInputStream(file, StandardOpenOption.DELETE_ON_CLOSE);
            } catch (IOException e) {
                try {
                    Files.delete(file);
                } catch (IOException ignore) {
                }
                throw new UncheckedIOException(e);
            }
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
