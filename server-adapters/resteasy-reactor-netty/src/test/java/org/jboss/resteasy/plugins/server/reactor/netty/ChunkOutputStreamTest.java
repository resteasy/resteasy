package org.jboss.resteasy.plugins.server.reactor.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.spi.WriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
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

public class ChunkOutputStreamTest {

    private static ByteArrayOutputStream baos;

    @Test
    public void testAsyncWrite_forSuccessfulWrite() throws IOException {
        baos = new ByteArrayOutputStream();
        try {
            final Sinks.Empty<Void> completionSink = Sinks.empty();

            final TestHttpServerResponse testHttpServerResponse = new BufferingHttpServerResponse();
            final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                    HttpMethod.POST,
                    testHttpServerResponse,
                    completionSink);
            final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                    httpServerResponse,
                    testHttpServerResponse,
                    completionSink);

            final String inputData = mkInputData();
            final InputStream in = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
            StepVerifier
                    .create(Mono.fromCompletionStage(ProviderHelper.writeToAndCloseInput(in, chunkOutputStream)))
                    .verifyComplete();
            Assertions.assertEquals(inputData, baos.toString());
        } finally {
            baos.close();
        }
    }

    @Test
    public void testAsyncWrite_withErrorOnSend() {
        final Sinks.Empty<Void> completionSink = Sinks.empty();

        final TestHttpServerResponse testHttpServerResponse = new ErrorHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                HttpMethod.POST,
                testHttpServerResponse,
                completionSink);
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink);

        final byte[] errorBytes = "ERROR".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                errorBytes,
                0,
                errorBytes.length);

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
                completionSink);
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink);

        final byte[] inputBytes = "DISCARD".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                inputBytes,
                0,
                inputBytes.length);

        StepVerifier
                .create(Mono
                        .fromCompletionStage(asyncWriteFuture)
                        .then(completionSink.asMono()
                                .timeout(Duration.ofSeconds(2))))
                .verifyError(WriterException.class);
    }

    @Test
    public void testAsyncWrite_forErrorOnCancel() {
        final Sinks.Empty<Void> completionSink = Sinks.empty();
        final StringBuffer buffer = new StringBuffer();
        completionSink.asMono().subscribe(v -> {
        }, err -> buffer.append(err.getClass().getName()));

        final TestHttpServerResponse testHttpServerResponse = new SuccessHttpServerResponse();
        final ReactorNettyHttpResponse httpServerResponse = new ReactorNettyHttpResponse(
                HttpMethod.POST,
                testHttpServerResponse,
                completionSink);
        final ChunkOutputStream chunkOutputStream = new ChunkOutputStream(
                httpServerResponse,
                testHttpServerResponse,
                completionSink);

        final byte[] inputBytes = "CANCEL".getBytes(StandardCharsets.UTF_8);
        final CompletableFuture<Void> asyncWriteFuture = chunkOutputStream.asyncWrite(
                inputBytes,
                0,
                inputBytes.length);

        StepVerifier.create(Mono.fromCompletionStage(asyncWriteFuture))
                .thenCancel()
                .verify();
        Assertions.assertEquals(WriterException.class.getName(), buffer.toString());
    }

    public String mkInputData() {
        final Random random = new Random();
        final Function<Integer, String> charFn = (index) -> random.nextInt() % 2 == 0 ? "a" : "b";
        return IntStream.range(0, 50000)
                .mapToObj(charFn::apply)
                .collect(Collectors.joining());
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
        public <S> NettyOutbound sendUsing(Callable<? extends S> callable,
                BiFunction<? super Connection, ? super S, ?> biFunction, Consumer<? super S> consumer) {
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
        public Mono<Void> sendWebsocket(
                BiFunction<? super WebsocketInbound, ? super WebsocketOutbound, ? extends Publisher<Void>> biFunction,
                WebsocketServerSpec websocketServerSpec) {
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

        @Override
        public HttpServerResponse trailerHeaders(final Consumer<? super HttpHeaders> trailerHeaders) {
            return null;
        }

        @Override
        public SocketAddress hostAddress() {
            return null;
        }

        @Override
        public SocketAddress connectionHostAddress() {
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            return null;
        }

        @Override
        public SocketAddress connectionRemoteAddress() {
            return null;
        }

        @Override
        public String scheme() {
            return null;
        }

        @Override
        public String connectionScheme() {
            return null;
        }

        @Override
        public String hostName() {
            return null;
        }

        @Override
        public int hostPort() {
            return 0;
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

    static class BufferingHttpServerResponse extends TestHttpServerResponse {

        @Override
        public NettyOutbound send(Publisher<? extends ByteBuf> publisher, Predicate<ByteBuf> predicate) {
            return then(Flux.from(publisher).map(byteBuf -> {
                try {
                    byteBuf.readBytes(baos, byteBuf.readableBytes());
                } catch (final IOException e) {
                    Assertions.fail("Error writing bytes to outputstream : " + e.getMessage());
                } finally {
                    byteBuf.release();
                }
                return true;
            }).then());
        }
    }

}
