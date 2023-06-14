package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.OutputStreamRequestContent;
import org.eclipse.jetty.http.HttpFields;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

public class JettyClientEngine implements AsyncClientHttpEngine {
    public static final String REQUEST_TIMEOUT_MS = JettyClientEngine.class + "$RequestTimeout";
    public static final String IDLE_TIMEOUT_MS = JettyClientEngine.class + "$IdleTimeout";
    // Yeah, this is the Jersey one, but there's no standard one and it makes more sense to reuse than make our own...
    public static final String FOLLOW_REDIRECTS = "jersey.config.client.followRedirects";

    private static final InvocationCallback<ClientResponse> NOP = new InvocationCallback<ClientResponse>() {
        @Override
        public void completed(ClientResponse response) {
        }

        @Override
        public void failed(Throwable throwable) {
        }
    };

    private final HttpClient client;

    public JettyClientEngine(final HttpClient client) {
        if (!client.isStarted()) {
            try {
                client.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.client = client;
    }

    @Override
    public SSLContext getSslContext() {
        return client.getSslContextFactory().getSslContext();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientResponse invoke(Invocation invocation) {
        Future<ClientResponse> future = submit((ClientInvocation) invocation, false, NOP, null);
        try {
            return future.get(1, TimeUnit.HOURS); // There's already an idle and connect timeout, do we need one here?
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw clientException(e, null);
        } catch (TimeoutException | ExecutionException e) {
            future.cancel(true);
            throw clientException(e.getCause(), null);
        }
    }

    @Override
    public <T> Future<T> submit(ClientInvocation invocation, boolean bufIn, InvocationCallback<T> callback,
            ResultExtractor<T> extractor) {
        return doSubmit(invocation, bufIn, callback, extractor);
    }

    @Override
    public <T> CompletableFuture<T> submit(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor,
            ExecutorService executorService) {
        return doSubmit(request, buffered, null, extractor);
    }

    private <T> CompletableFuture<T> doSubmit(ClientInvocation invocation, boolean buffered, InvocationCallback<T> callback,
            ResultExtractor<T> extractor) {
        final ExecutorService asyncExecutor = invocation.asyncInvocationExecutor();

        final Request request = client.newRequest(invocation.getUri());
        final CompletableFuture<T> future = new RequestFuture<T>(request);

        invocation.getMutableProperties().forEach(request::attribute);
        request.method(invocation.getMethod());
        request.headers(mutableHeaders -> invocation.getHeaders().asMap()
                .forEach((h, vs) -> vs.forEach(v -> mutableHeaders.add(h, v))));
        configureTimeout(request);
        if (request.getAttributes().get(FOLLOW_REDIRECTS) == Boolean.FALSE) {
            request.followRedirects(false);
        }

        if (invocation.getEntity() != null) {
            final OutputStreamRequestContent contentOut = new OutputStreamRequestContent(
                    Objects.toString(invocation.getHeaders().getMediaType(), null));
            asyncExecutor.execute(() -> {
                try {
                    try (OutputStream bodyOut = contentOut.getOutputStream()) {
                        invocation.writeRequestBody(bodyOut);
                    }
                } catch (Exception e) { // Also catch any exception thrown from close
                    future.completeExceptionally(e);
                    if (callback != null) {
                        callback.failed(e);
                    }
                }
            });
            request.body(contentOut);
        }

        request.send(new InputStreamResponseListener() {
            private ClientResponse cr;

            @Override
            @SuppressWarnings("unchecked")
            public void onHeaders(Response response) {
                super.onHeaders(response);
                InputStream inputStream = getInputStream();
                cr = new JettyClientResponse(invocation.getClientConfiguration(), inputStream);
                cr.setProperties(invocation.getMutableProperties());
                cr.setStatus(response.getStatus());
                cr.setHeaders(extract(response.getHeaders()));
                asyncExecutor.submit(() -> {
                    try {
                        if (buffered) {
                            cr.bufferEntity();
                        }
                        complete(extractor == null ? (T) cr : extractor.extractResult(cr));
                    } catch (Exception e) {
                        try {
                            inputStream.close();
                        } catch (Exception e1) {
                            e.addSuppressed(e1);
                        }
                        onFailure(response, e);
                    }
                });
            }

            @Override
            public void onFailure(Response response, Throwable failure) {
                super.onFailure(response, failure);
                failed(failure);
            }

            private void complete(T result) {
                future.complete(result);
                if (callback != null) {
                    callback.completed(result);
                }
            }

            private void failed(Throwable t) {
                final RuntimeException x = clientException(t, cr);
                future.completeExceptionally(x);
                if (callback != null) {
                    callback.failed(x);
                }
            }
        });
        return future;
    }

    private void configureTimeout(final Request request) {
        final Object timeout = request.getAttributes().get(REQUEST_TIMEOUT_MS);
        final Object idleTimeout = request.getAttributes().get(IDLE_TIMEOUT_MS);
        final long timeoutMs = parseTimeoutMs(timeout);
        final long idleTimeoutMs = parseTimeoutMs(idleTimeout);
        if (timeoutMs > 0) {
            request.timeout(timeoutMs, TimeUnit.MILLISECONDS);
        }

        if (idleTimeoutMs > 0) {
            request.idleTimeout(idleTimeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    private long parseTimeoutMs(final Object timeout) {
        final long timeoutMs;
        if (timeout instanceof Duration) {
            timeoutMs = ((Duration) timeout).toMillis();
        } else if (timeout instanceof Number) {
            timeoutMs = ((Number) timeout).intValue();
        } else if (timeout != null) {
            timeoutMs = Integer.parseInt(timeout.toString());
        } else {
            timeoutMs = -1;
        }
        return timeoutMs;
    }

    @Override
    public void close() {
        try {
            client.stop();
        } catch (Exception e) {
            throw new RuntimeException("Unable to close JettyHttpEngine", e);
        }
    }

    MultivaluedMap<String, String> extract(HttpFields headers) {
        final MultivaluedMap<String, String> extracted = new MultivaluedHashMap<>();
        headers.forEach(h -> extracted.add(h.getName(), h.getValue()));
        return extracted;
    }

    private static RuntimeException clientException(Throwable ex, jakarta.ws.rs.core.Response clientResponse) {
        RuntimeException ret;
        if (ex == null) {
            final NullPointerException e = new NullPointerException();
            e.fillInStackTrace();
            ret = new ProcessingException(e);
        } else if (ex instanceof WebApplicationException) {
            ret = (WebApplicationException) ex;
        } else if (ex instanceof ProcessingException) {
            ret = (ProcessingException) ex;
        } else if (clientResponse != null) {
            ret = new ResponseProcessingException(clientResponse, ex);
        } else {
            ret = new ProcessingException(ex);
        }
        ret.fillInStackTrace();
        return ret;
    }

    static class RequestFuture<T> extends CompletableFuture<T> {
        private final Request request;

        RequestFuture(final Request request) {
            this.request = request;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            final boolean cancelled = super.cancel(mayInterruptIfRunning);
            if (mayInterruptIfRunning && cancelled) {
                request.abort(new CancellationException());
            }
            return cancelled;
        }
    }
}
