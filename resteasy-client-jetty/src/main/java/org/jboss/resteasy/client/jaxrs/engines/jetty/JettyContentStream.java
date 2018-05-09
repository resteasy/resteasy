package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Callback;

class JettyContentStream extends OutputStream {
    private static final int BUF_SIZE = 16 * 1024;

    private final ByteBufferPool bufs;
    private final DeferredContentProvider out;

    private ByteBuffer buf;

    JettyContentStream(ByteBufferPool bufs, DeferredContentProvider out) {
        this.bufs = bufs;
        this.out = out;
        buf = acquire();
    }

    @Override
    public void write(int b) throws IOException {
        checkClose();
        if (!buf.hasRemaining()) {
            flush();
        }
        buf.put((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int rem) {
        checkClose();
        while (true) {
            if (!buf.hasRemaining() || rem == 0) {
                flush();
                return;
            }
            final int r = Math.min(buf.remaining(), rem);
            buf.put(b, off, r);
            off += r;
            rem -= r;
            flush();
        }
    }

    @Override
    public void flush() {
        checkClose();
        buf.flip();
        if (buf.limit() == 0) {
            bufs.release(buf);
            return;
        }
        out.offer(buf, new ReleaseCallback(bufs, buf));
        buf = acquire();
    }

    @Override
    public void close() throws IOException {
        if (buf != null) {
            flush();
            buf = null;
        }
    }

    private void checkClose() {
        if (out.isClosed()) {
            throw new IllegalStateException("closed");
        }
    }

    private ByteBuffer acquire() {
        final ByteBuffer b = bufs.acquire(BUF_SIZE, false);
        b.limit(b.capacity());
        return b;
    }
}

class ReleaseCallback implements Callback {

    private final ByteBufferPool bufs;
    private final ByteBuffer buf;

    ReleaseCallback(ByteBufferPool bufs, ByteBuffer buf) {
        this.bufs = bufs;
        this.buf = buf;
    }

    @Override
    public void succeeded() {
        bufs.release(buf);
    }

    @Override
    public void failed(Throwable x) {
        bufs.release(buf);
    }
}
