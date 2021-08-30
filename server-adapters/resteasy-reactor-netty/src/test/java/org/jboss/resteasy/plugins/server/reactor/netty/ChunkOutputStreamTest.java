package org.jboss.resteasy.plugins.server.reactor.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.jboss.resteasy.plugins.server.reactor.netty.ChunkOutputStream.RESPONSE_WRITE_ABORTED_ON_CANCEL;
import static org.jboss.resteasy.plugins.server.reactor.netty.ChunkOutputStream.RESPONSE_WRITE_ABORTED_ON_DISCARD;

public class ChunkOutputStreamTest {

    @Test
    public void testAsyncWrite_withErrorOnSend() {
        final Sinks.Empty<Void> completionSink = Sinks.empty();

        final TestHttpServerResponse testHttpServerResponse = new ErrorHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                HttpMethod.POST,
                testHttpServerResponse,
                completionSink
        );
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink
        );

        final byte[] errorBytes = "ERROR".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                errorBytes,
                0,
                errorBytes.length
        );

        StepVerifier.create(Mono.fromCompletionStage(asyncWriteFuture).then(completionSink.asMono()))
                .verifyError(AbortedException.class);
    }

    @Test
    public void testAsyncWrite_forErrorOnDiscard() {
        final Sinks.Empty<Void> completionSink = Sinks.empty();

        final TestHttpServerResponse testHttpServerResponse = new DiscardingHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                HttpMethod.POST,
                testHttpServerResponse,
                completionSink
        );
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink
        );

        final byte[] errorBytes = "DISCARD".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                errorBytes,
                0,
                errorBytes.length
        );

        StepVerifier.create(Mono.fromCompletionStage(asyncWriteFuture).then(completionSink.asMono()))
                .verifyError(RESPONSE_WRITE_ABORTED_ON_DISCARD.getClass());
    }

    @Test
    public void testAsyncWrite_forErrorOnCancel() {
        final Sinks.Empty<Void> completionSink = Sinks.empty();
        final StringBuffer sb = new StringBuffer();
        completionSink.asMono().subscribe(v -> {}, err -> sb.append(err.getClass().getName()));

        final TestHttpServerResponse testHttpServerResponse = new SuccessHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                HttpMethod.POST,
                testHttpServerResponse,
                completionSink
        );
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink
        );

        final byte[] errorBytes = "CANCEL".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                errorBytes,
                0,
                errorBytes.length
        );

        StepVerifier.create(Mono.fromCompletionStage(asyncWriteFuture))
                .thenCancel()
                .verify();
        Assert.assertEquals(RESPONSE_WRITE_ABORTED_ON_CANCEL.getClass().getName(), sb.toString());
    }

    abstract static class TestHttpServerResponse implements HttpServerResponse {

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        public abstract NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate);

        @Override
        public NettyOutbound sendObject(Publisher<?> publisher, Predicate<Object> predicate) {
            return null;
        }

        @Override
        public NettyOutbound sendObject(Object o) {
            return null;
        }

        @Override
        public <S> NettyOutbound sendUsing(Callable<? extends S> callable, BiFunction<? super Connection, ? super S, ?> biFunction, Consumer<? super S> consumer) {
            return null;
        }

        @Override
        public HttpServerResponse addCookie(Cookie cookie) {
            return null;
        }

        @Override
        public HttpServerResponse addHeader(CharSequence charSequence, CharSequence charSequence1) {
            return null;
        }

        @Override
        public HttpServerResponse chunkedTransfer(boolean b) {
            return null;
        }

        @Override
        public HttpServerResponse withConnection(Consumer<? super Connection> consumer) {
            return null;
        }

        @Override
        public HttpServerResponse compression(boolean b) {
            return null;
        }

        @Override
        public boolean hasSentHeaders() {
            return false;
        }

        @Override
        public HttpServerResponse header(CharSequence charSequence, CharSequence charSequence1) {
            return null;
        }

        @Override
        public HttpServerResponse headers(HttpHeaders httpHeaders) {
            return null;
        }

        @Override
        public HttpServerResponse keepAlive(boolean b) {
            return null;
        }

        @Override
        public HttpHeaders responseHeaders() {
            return null;
        }

        @Override
        public Mono<Void> send() {
            return null;
        }

        @Override
        public NettyOutbound sendHeaders() {
            return null;
        }

        @Override
        public Mono<Void> sendNotFound() {
            return null;
        }

        @Override
        public Mono<Void> sendRedirect(String s) {
            return null;
        }

        @Override
        public Mono<Void> sendWebsocket(BiFunction<? super WebsocketInbound, ? super WebsocketOutbound, ? extends Publisher<Void>> biFunction, WebsocketServerSpec websocketServerSpec) {
            return null;
        }

        @Override
        public HttpServerResponse sse() {
            return null;
        }

        @Override
        public HttpResponseStatus status() {
            return null;
        }

        @Override
        public HttpServerResponse status(HttpResponseStatus httpResponseStatus) {
            return null;
        }

        @Override
        public Map<CharSequence, List<Cookie>> allCookies() {
            return null;
        }

        @Override
        public Map<CharSequence, Set<Cookie>> cookies() {
            return null;
        }

        @Override
        public String fullPath() {
            return null;
        }

        @Override
        public String requestId() {
            return null;
        }

        @Override
        public boolean isKeepAlive() {
            return false;
        }

        @Override
        public boolean isWebsocket() {
            return false;
        }

        @Override
        public HttpMethod method() {
            return null;
        }

        @Override
        public String uri() {
            return null;
        }

        @Override
        public HttpVersion version() {
            return null;
        }
    }

    static class DiscardingHttpServerResponse extends TestHttpServerResponse {
        @Override
        public NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate) {
            return then(Flux.from(publisher).skipLast(1).then());
        }
    }

    static class ErrorHttpServerResponse extends TestHttpServerResponse {
        @Override
        public NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate) {
            return then(Mono.error(AbortedException.beforeSend()));
        }
    }

    static class SuccessHttpServerResponse extends TestHttpServerResponse {
        @Override
        public NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate) {
            return then(Flux.from(publisher).then());
        }
    }

}
