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
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChunkOutputStreamTest {

    @Test
    public void testAsyncWrite_withErrorOnSend() throws ExecutionException, InterruptedException {
        final Sinks.Empty<Void> completionSink = Sinks.empty();
        final MockHttpServerResponse mockHttpServerResponse = new MockHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
               HttpMethod.POST,
                mockHttpServerResponse,
               completionSink
        );
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                mockHttpServerResponse,
                completionSink
        );
        byte[] errorBytes = "ERROR".getBytes(StandardCharsets.UTF_8);
        CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                errorBytes,
                0,
                errorBytes.length
        );
        Throwable error = null;
        try {
            asyncWriteFuture.get();
        } catch (Exception e) {
            error = e.getCause();
        }
        Assert.assertTrue(error instanceof AbortedException);
    }

    static class MockHttpServerResponse implements HttpServerResponse {

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate) {
            return this.then(Mono.from(publisher).map(byteBuf ->{
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(byteBuf.readerIndex(), bytes).toString();
                if("ERROR".equalsIgnoreCase(new String(bytes, StandardCharsets.UTF_8))) {
                    throw AbortedException.beforeSend();
                }
                return "Success";
            }).then());
        }

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
}
