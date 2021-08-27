package org.jboss.resteasy.plugins.server.reactor.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.WriterException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

/**
 * This is the output stream leveraged by {@link
 * ReactorNettyHttpResponse#getOutputStream}.  It provides the heavy lifting
 * for actually transferring the bytes written by RestEasy to a {@link NettyOutbound},
 * which is what reactor-netty works with.  Most of the heavy
 * lifting occurs in {@link #asyncWrite(byte[], int, int)}.
 */
public class ChunkOutputStream extends AsyncOutputStream {

    /**
     * This is the {@link Mono} that we return from {@link ReactorNettyJaxrsServer.Handler#handle(HttpServerRequest,
     * HttpServerResponse)}
     */
    protected final Sinks.Empty<Void> completionSink;

    /**
     * Indicates that we've starting sending the response bytes.
     */
    private volatile boolean started;

    private final NettyOutbound nettyOutbound;

    /**
     * Used in {@link NettyOutbound#send(Publisher, Predicate)} to trigger flushing the bytes before the {@link
     * CompletableFuture} returned from `NettyOutbound#then().toFuture()` completes.
     */
    private static final Predicate<ByteBuf> FLUSH_ON_EACH_WRITE = bb -> true;

    private final ReactorNettyHttpResponse parentResponse;

    private static final Throwable RESPONSE_WRITE_ERROR
            = new WriterException("Cannot complete response write");

    ChunkOutputStream(
            final ReactorNettyHttpResponse parentResponse,
            final HttpServerResponse reactorNettyResponse,
            final Sinks.Empty<Void> completionSink
    ) {
        this.completionSink = completionSink;
        this.parentResponse = Objects.requireNonNull(parentResponse);
        this.nettyOutbound = Objects.requireNonNull(reactorNettyResponse);
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

    @Override
    public CompletableFuture<Void> asyncWrite(final byte[] bs, int offset, int length) {
        try {
            if (!started) {
                parentResponse.committed();
                started = true;
            }
            byte[] bytes = bs;
            if (offset != 0 || length != bs.length) {
                bytes = Arrays.copyOfRange(bs, offset, offset + length);
            }

            return nettyOutbound
                    .send(Mono.just(Unpooled.wrappedBuffer(bytes)), FLUSH_ON_EACH_WRITE)
                    .then()
                    .doOnError(err -> completionSink.emitError(err, Sinks.EmitFailureHandler.FAIL_FAST))
                    .doOnCancel(() -> completionSink.emitError(
                            RESPONSE_WRITE_ERROR,
                            Sinks.EmitFailureHandler.FAIL_FAST
                    ))
                    .doOnDiscard(
                            Void.class,
                            v -> completionSink.emitError(
                                    RESPONSE_WRITE_ERROR,
                                    Sinks.EmitFailureHandler.FAIL_FAST
                    ))
                    .toFuture();
        } catch (final Exception e) {
            completionSink.emitError(e, Sinks.EmitFailureHandler.FAIL_FAST);
            final CompletableFuture<Void> cf = new CompletableFuture<>();
            cf.completeExceptionally(e);
            return cf;
        }
    }

    @Override
    public void close() throws IOException {
        SinkSubscriber.subscribe(completionSink, Mono.empty());
    }
}
