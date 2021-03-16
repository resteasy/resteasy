package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.spi.AsyncOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * This is the output stream leveraged by {@link
 * ReactorNettyHttpResponse#getOutputStream}.  It provides the heavy lifting
 * for actually transfering the bytes written by RestEasy to a {@link
 * Flux<byte[]>}, which is what reactor-netty works with.  Most of the heavy
 * lifting occurs in {@link #asyncWrite(byte[], int, int)}.
 */
public class ChunkOutputStream extends AsyncOutputStream {

   private static final EmitFailureHandler EMIT_FAILURE_HANDLER = EmitFailureHandler.FAIL_FAST;

   private final ReactorNettyHttpResponse parentResponse;

   /**
    * This is the {@link Mono} that we return from
    * {@link ReactorNettyJaxrsServer.Handler#handle(HttpServerRequest, HttpServerResponse)}
    */
   private final Sinks.Empty<Void> completionSink;

   /**
    * Indicates that we've starting sending the response bytes.
    */
   private volatile boolean started;

   /**
    * This is ultimately think 'sink' that we write bytes to in
    * {@link #asyncWrite(byte[], int, int)}.
    */
   private Sinks.Many<Tuple2<byte[], CompletableFuture<Void>>> byteSink;

   /**
    * This is used to establish {@link #byteSink} upon the first writing of bytes.
    */
   private final Supplier<Sinks.Many<Tuple2<byte[], CompletableFuture<Void>>>> byteSinkSupplier;

   ChunkOutputStream(
       final ReactorNettyHttpResponse parentResponse,
       final HttpServerResponse reactorNettyResponse,
       final Sinks.Empty<Void> completionSink
   ) {
       this.parentResponse = Objects.requireNonNull(parentResponse);
       this.completionSink = Objects.requireNonNull(completionSink);
       Objects.requireNonNull(reactorNettyResponse);
       this.byteSinkSupplier = () -> {
           final Sinks.Many<Tuple2<byte[], CompletableFuture<Void>>> outSink = Sinks.many().multicast().onBackpressureBuffer();
           final Flux<byte[]> byteFlux = outSink.asFlux().map(tup -> {
                   tup.getT2().complete(null);
                   return tup.getT1();
               });

           SinkSubscriber.subscribe(completionSink, Mono.from(reactorNettyResponse.sendByteArray(byteFlux)));

           return outSink;
       };
   }

   @Override
   public void write(int b) {
       write(new byte[] {(byte)b}, 0, 1);
   }

   @Override
   public void close() throws IOException {
       if (!started || byteSink == null) {
           SinkSubscriber.subscribe(completionSink, Mono.empty());
       } else {
           byteSink.emitComplete(EMIT_FAILURE_HANDLER);
       }
   }

   @Override
   public void write(byte[] bs, int off, int len) {
       try {
           asyncWrite(bs, off, len).get();
       } catch (final InterruptedException ie) {
           Thread.currentThread().interrupt();
           throw new RuntimeException(ie);
       } catch (final ExecutionException ee) {
           throw new RuntimeException(ee);
       }
   }

   @Override
   public void flush() {
       try {
           asyncFlush().get();
       } catch (final InterruptedException ie) {
           Thread.currentThread().interrupt();
           throw new RuntimeException(ie);
       } catch (final ExecutionException ee) {
           throw new RuntimeException(ee);
       }
   }

   @Override
   public CompletableFuture<Void> asyncFlush() {

       // [AG] Discuss with @crankydillo.  Here is my understanding:
       //   - asyncFlush is used mainly for SSE.
       //   - The idea seems to be to send the element immediately without
       //     waiting for the rest of the elements.  Anyway, that's what SSE is.
       //   - But, at the same time, it is trying to not overload.  So, kind of
       //     doing backpressure.  Only if the previous flush is complete,
       //     then request the next element from the app.
       //   - The backpressure mechanism is already built into Reactor Netty.  But,
       //     the question is how to communicate that.
       //   - Please see https://projectreactor.io/docs/netty/release/reference/index.html#_sse.
       //     It reads `The flushing strategy is "flush after every element" emitted
       //     by the provided Publisher`.  So, we do not need to call flush individually.
       //
       //   In summary, we are not controlling flushing, instead Reactor Netty does.
       //   Also, this#asyncWrite will manage the backpressure.  I assume Reactor Netty
       //   will backpressure if it cannot flush, and that would mean an element won't be
       //   requested so asyncWrite won't complete until the element is pulled.  Also,
       //   asyncFlush is called right after an asyncWrite.  So, asyncFlush looks
       //   useless.

       return CompletableFuture.completedFuture(null);
   }

   @Override
   public CompletableFuture<Void> asyncWrite(final byte[] bs, int offset, int length) {
        final CompletableFuture<Void> cf = new CompletableFuture<>();
        if (!started) {
            byteSink = byteSinkSupplier.get();
            parentResponse.committed();
            started = true;
        }

        byte[] bytes = bs;
        if (offset != 0 || length != bs.length) {
            bytes = Arrays.copyOfRange(bs, offset, offset + length);
        }
        byteSink.emitNext(Tuples.of(bytes, cf), EMIT_FAILURE_HANDLER);
        return cf;
   }
}
