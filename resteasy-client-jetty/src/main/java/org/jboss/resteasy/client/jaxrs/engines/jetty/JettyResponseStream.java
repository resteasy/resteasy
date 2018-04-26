package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.ToIntFunction;

import org.eclipse.jetty.util.Callback;

class JettyResponseStream extends InputStream {
    private final Deque<Chunk> chunks = new LinkedList<>();
    private Chunk readTop;
    private volatile boolean closed;

    void offer(ByteBuffer content, Callback callback) {
        if (closed) {
            final IllegalStateException x = new IllegalStateException("closed");
            callback.failed(x);
            throw x;
        }
        chunks.add(new Chunk(content, callback));
    }

    @Override
    public int read() throws IOException {
        return read0(chunk -> chunk.buf.get());
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return read0(chunk -> {
            final int r = Math.min(chunk.buf.remaining(), len);
            chunk.buf.get(b, off, r);
            return r;
        });
    }

    private int read0(ToIntFunction<Chunk> reader) throws IOException {
        if (closed) {
            throw new IllegalStateException("closed");
        }
        if (readTop == null) {
            readTop = chunks.pollFirst();
        }
        if (readTop == null || closed) {
            return -1;
        }

        final int result = reader.applyAsInt(readTop);
        if (!readTop.buf.hasRemaining()) {
            readTop.callback.succeeded();
            readTop = null;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (readTop != null) {
            readTop.callback.succeeded();
            readTop = null;
        }
        chunks.removeIf(c -> {
            c.callback.succeeded();
            return true;
        });
    }

    static class Chunk {
        final ByteBuffer buf;
        final Callback callback;

        Chunk(ByteBuffer buf, Callback callback) {
            this.buf = buf;
            this.callback = callback;
        }
    }
}
