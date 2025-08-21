package org.jboss.resteasy.client.jaxrs.engines.vertx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

/**
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class InputStreamAdapter extends InputStream {

    private final Object lock = new Object();
    private final ReadStream<Buffer> stream;
    private Buffer pending = Buffer.buffer();
    private boolean paused;
    private boolean ended;
    private Throwable failure;
    private final long maxPendingSize;

    public InputStreamAdapter(final ReadStream<Buffer> stream) {
        this(stream, 256);
    }

    public InputStreamAdapter(final ReadStream<Buffer> stream, final long maxPendingSize) {
        this.stream = stream;
        this.maxPendingSize = maxPendingSize;

        stream.handler(this::onChunk);
        stream.endHandler(this::onEnd);
        stream.exceptionHandler(this::onError);
    }

    private void onChunk(Buffer chunk) {
        synchronized (lock) {
            pending.appendBuffer(chunk);
            if (pending.length() > maxPendingSize) {
                paused = true;
                stream.pause();
            }
            lock.notifyAll();
        }
    }

    private void onEnd(Void v) {
        synchronized (lock) {
            ended = true;
            lock.notifyAll();
        }
    }

    private void onError(Throwable cause) {
        synchronized (lock) {
            failure = cause;
            ended = true;
            lock.notifyAll();
        }
    }

    private byte[] buffer = new byte[1];

    @Override
    public int read() throws IOException {
        int val = read(buffer, 0, 1);
        if (val == -1) {
            return -1;
        }
        return buffer[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        synchronized (lock) {
            if (len == 0) {
                if (ended) {
                    if (failure != null) {
                        throw new IOException(failure);
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            }
            while (true) {
                if (pending.length() > 0) {
                    int amount = Math.min(pending.length(), len);
                    pending.getBytes(0, amount, b);
                    pending = pending.getBuffer(amount, pending.length());
                    if (pending.length() == 0L && paused) {
                        paused = false;
                        stream.resume();
                    }
                    return amount;
                } else {
                    if (ended) {
                        if (failure != null) {
                            throw new IOException(failure);
                        } else {
                            return -1;
                        }
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                }
            }
        }
    }
}
