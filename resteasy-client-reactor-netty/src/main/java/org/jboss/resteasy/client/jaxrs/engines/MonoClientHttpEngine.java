package org.jboss.resteasy.client.jaxrs.engines;

import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.jboss.resteasy.util.HttpHeaderNames.CONTENT_LENGTH;

public interface MonoClientHttpEngine extends ReactiveClientHttpEngine {

    public static class MonoUnit<T> implements ReactiveClientHttpEngine.Unit<T> {
        private final Mono<T> delegate;

        public MonoUnit(Mono<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Mono<T> get() {
            return delegate;
        }

        @Override
        public CompletableFuture<T> toFuture() {
            return delegate.toFuture();
        }

        @Override
        public void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError, Runnable onComplete) {
            delegate.subscribe(onSuccess, onError, onComplete);
        }
    }

    <T> Mono<T> theRealSubmitRx(
        final ClientInvocation request,
        final boolean buffered,
        final AsyncClientHttpEngine.ResultExtractor<T> extractor);

    /*
    @Override
    public <T, U> Unit<T, U> submitRx(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor) {
        return null;
    }

     */

    @Override
    default <T> MonoUnit<T> submitRx(ClientInvocation request, boolean buffered, AsyncClientHttpEngine.ResultExtractor<T> extractor) {
        return new MonoUnit<>(theRealSubmitRx(request, buffered, extractor));
    }

    @Override
    default <T> MonoUnit<T> just(final T t) {
        return new MonoUnit<>(Mono.just(t));
    }

    @Override
    default MonoUnit<Throwable> error(final Exception e) {
        return new MonoUnit<>(Mono.error(e));
    }

    @Override
    default <T> Future<T> submit(final ClientInvocation request,
                                final boolean buffered,
                                final InvocationCallback<T> callback,
                                final AsyncClientHttpEngine.ResultExtractor<T> extractor) {

        return submit(request, buffered, extractor, null)
            .whenComplete((response, throwable) -> {
                if(callback != null) {
                    if (throwable != null) callback.failed(throwable);
                    else callback.completed(response);
                }
            });
    }

    @Override
    default <K> CompletableFuture<K> submit(final ClientInvocation request,
                                           final boolean buffered,
                                           final AsyncClientHttpEngine.ResultExtractor<K> extractor,
                                           final ExecutorService executorService
    ) {
        return theRealSubmitRx(request, buffered, extractor).toFuture();
    }

    @Override
    default Response invoke(Invocation request) {
        final Future<ClientResponse> future = submit((ClientInvocation) request, false, null, response -> response);

        try {
            return future.get();
        } catch (InterruptedException e) {
            future.cancel(true);
            throw clientException(e, null);
        } catch (ExecutionException e) {
            throw clientException(e.getCause(), null);
        }
    }

    static RuntimeException clientException(Throwable ex, Response clientResponse) {
        RuntimeException ret;
        if (ex == null) {
            ret = new ProcessingException(new NullPointerException());
        } else if (ex instanceof WebApplicationException) {
            ret = (WebApplicationException) ex;
        } else if (ex instanceof ProcessingException) {
            ret = (ProcessingException) ex;
        } else if (clientResponse != null) {
            ret = new ResponseProcessingException(clientResponse, ex);
        } else {
            ret = new ProcessingException(ex);
        }
        return ret;
    }
}
