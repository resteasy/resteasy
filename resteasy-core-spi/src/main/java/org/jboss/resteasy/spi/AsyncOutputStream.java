package org.jboss.resteasy.spi;

import java.io.OutputStream;
import java.util.concurrent.CompletionStage;

/**
 * OutputStream which supports async IO operations. Use these operations if you need to support async IO.
 */
public abstract class AsyncOutputStream extends OutputStream {

    /**
     * Flushes this async output stream.
     * @return a {@link CompletionStage} notified on completion of the flush operation.
     */
    public abstract CompletionStage<Void> asyncFlush();

    /**
     * Writes to this async output stream. Equivalent to {@code asyncWrite(bytes, 0, bytes.length}.
     * @param bytes the bytes to write
     * @return a {@link CompletionStage} notified on completion of the write operation.
     */
    public CompletionStage<Void> asyncWrite(byte[] bytes) {
        return asyncWrite(bytes, 0, bytes.length);
    }

    /**
     * Writes to this async output stream.
     * @param bytes the bytes to write
     * @param offset the offset from which to start writing in the given byte array.
     * @param length the number of bytes to write from the given byte array
     * @return a {@link CompletionStage} notified on completion of the write operation.
     */
    public abstract CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length);
}
