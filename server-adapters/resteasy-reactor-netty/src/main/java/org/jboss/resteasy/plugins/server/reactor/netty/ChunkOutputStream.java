package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.spi.AsyncOutputStream;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This is the output stream leveraged by {@link
 * ReactorNettyHttpResponse#getOutputStream}.  It provides the heavy lifting
 * for actually transferring the bytes written by RestEasy to a {@link NettyOutbound},
 * which is what reactor-netty works with.  Most of the heavy
 * lifting occurs in {@link #asyncWrite(byte[], int, int)}.
 */
abstract class ChunkOutputStream extends AsyncOutputStream {

    /**
     * Determines whether {@link FlushingOutputStream} or {@link ReleaseBasedOutputStream} is used.
     */
    private static final boolean USE_FLUSHING =
        Boolean.parseBoolean(System.getProperty("resteasy.server.reactor-netty.use-flushing", "true"));

    /**
     * This is the {@link Mono} that we return from {@link ReactorNettyJaxrsServer.Handler#handle(HttpServerRequest,
     * HttpServerResponse)}
     */
    protected final Sinks.Empty<Void> completionSink;

    public ChunkOutputStream(Sinks.Empty<Void> completionSink) {
        this.completionSink = completionSink;
    }

    static ChunkOutputStream create(
        final ReactorNettyHttpResponse parentResp,
        final HttpServerResponse nettyResp,
        final Sinks.Empty<Void> completionSink
    ) {
        return USE_FLUSHING
            ? new FlushingOutputStream(parentResp, nettyResp, completionSink)
            : new ReleaseBasedOutputStream(parentResp, nettyResp, completionSink);
    }

    @Override
    public void write(int b) {
        write(new byte[]{(byte) b}, 0, 1);
    }

    @Override
    public void write(byte[] bs, int off, int len) {
        try {
            asyncWrite(bs, off, len).toCompletableFuture().get();
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ReactorNettySendException(ie);
        } catch (final ExecutionException ee) {
            throw new ReactorNettySendException(ee);
        }
    }

    @Override
    public void flush() {
        try {
            asyncFlush().get();
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ReactorNettySendException(ie);
        } catch (final ExecutionException ee) {
            throw new ReactorNettySendException(ee);
        }
    }

    @Override
    public CompletableFuture<Void> asyncFlush() {
        // Everything flows through asyncWrite and we are flushing on each call, so we
        // will treat that as a no-op for now, assuming that callers of this would have
        // chained this onto an asyncWrite.  I hope that doesn't mess up SSE..
        return CompletableFuture.completedFuture(null);
    }

    static class ReactorNettySendException extends RuntimeException {
        public ReactorNettySendException(Throwable cause) {
            super(cause);
        }
    }
}
