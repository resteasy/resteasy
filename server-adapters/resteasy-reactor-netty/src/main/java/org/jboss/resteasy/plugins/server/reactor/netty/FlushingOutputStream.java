package org.jboss.resteasy.plugins.server.reactor.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

class FlushingOutputStream extends ChunkOutputStream {

    /**
     * Used in {@link NettyOutbound#send(Publisher, Predicate)} to trigger flushing the bytes before the {@link
     * CompletableFuture} returned from `NettyOutbound#then().toFuture()` completes.
     */
    private static final Predicate<ByteBuf> FLUSH_ON_EACH_WRITE = bb -> true;

    private final ReactorNettyHttpResponse parentResponse;

    /**
     * Indicates that we've starting sending the response bytes.
     */
    private volatile boolean started;

    private final NettyOutbound nettyOutbound;

    FlushingOutputStream(
        final ReactorNettyHttpResponse parentResponse,
        final HttpServerResponse reactorNettyResponse,
        final Sinks.Empty<Void> completionSink
    ) {
        super(completionSink);
        this.parentResponse = Objects.requireNonNull(parentResponse);
        this.nettyOutbound = Objects.requireNonNull(reactorNettyResponse);
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

            // I wonder what happens on cancellation...
            return nettyOutbound
                .send(Mono.just(Unpooled.wrappedBuffer(bytes)), FLUSH_ON_EACH_WRITE)
                .then()
                .doOnError(err -> completionSink.emitError(err, Sinks.EmitFailureHandler.FAIL_FAST))
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
