package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.ByteBufferPool;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

public class JettyClientEngine implements AsyncClientHttpEngine {
    private static final AtomicBoolean WARN_BUF = new AtomicBoolean();
    public static final String REQUEST_TIMEOUT_MS = JettyClientEngine.class + "$RequestTimeout";
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

    public JettyClientEngine(HttpClient client) {
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
    public ClientResponse invoke(ClientInvocation invocation) {
        Future<ClientResponse> future = submit(invocation, true, NOP, null);
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

    /**
     * Implementation note: due to lack of asynchronous message decoders the request must either be buffered,
     * or it must have a {@code null} extractor and type parameter {@code <T>} must be {@link ClientResponse},
     * which will read the data through its stream.  It is not possible to use the synchronous JAX-RS message
     * decoding infrastructure without buffering or spinning up auxiliary threads (arguably more expensive than buffering).
     *
     * @see AsyncClientHttpEngine#submit(ClientInvocation, boolean, InvocationCallback, org.jboss.resteasy.client.jaxrs.AsyncClientHttpEngine.ResultExtractor)
     */
    @Override
    public <T> Future<T> submit(ClientInvocation invocation, boolean bufIn, InvocationCallback<T> callback, ResultExtractor<T> extractor) {
        final boolean buffered;
        if (!bufIn && extractor != null) {
            if (!WARN_BUF.getAndSet(true)) {
                Logger LOG = Logger.getLogger(JettyClientEngine.class);
                LOG.error("TODO: ResultExtractor is synchronous and may not be used without buffering - forcing buffer mode.");
            }
            buffered = true;
        } else {
            buffered = bufIn;
        }

        final Request request = client.newRequest(invocation.getUri());
        final CompletableFuture<T> future = new RequestFuture<T>(request);
        // readEntity calls releaseConnection which calls cancel, so don't let that interrupt us
        final AtomicBoolean completing = new AtomicBoolean();

        invocation.getMutableProperties().forEach(request::attribute);
        request.method(invocation.getMethod());
        invocation.getHeaders().asMap().forEach((h, vs) -> vs.forEach(v -> request.header(h, v)));
        configureTimeout(request);
        if (request.getAttributes().get(FOLLOW_REDIRECTS) == Boolean.FALSE) {
            request.followRedirects(false);
        }

        if (invocation.getEntity() != null) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                invocation.writeRequestBody(os);
            } catch (IOException e) {
                future.completeExceptionally(e);
                if (callback != null) {
                    callback.failed(e);
                }
                return future;
            }
            request.content(new BytesContentProvider(os.toByteArray()));
        }

        request.send(new Response.Listener.Adapter() {
            private ClientResponse cr;
            private JettyResponseStream stream = new JettyResponseStream();

            @Override
            public void onHeaders(Response response) {
                cr = new JettyClientResponse(invocation.getClientConfiguration(), stream, () -> {
                    if (!completing.get()) {
                        future.cancel(true);
                    }
                });
                cr.setProperties(invocation.getMutableProperties());
                cr.setStatus(response.getStatus());
                cr.setHeaders(extract(response.getHeaders()));
                if (!buffered) {
                    complete();
                }
            }

            @Override
            public void onContent(Response response, ByteBuffer buf) {
                final ByteBufferPool bufs = client.getByteBufferPool();
                final ByteBuffer copy = bufs.acquire(buf.remaining(), false);
                copy.limit(buf.remaining());
                copy.put(buf);
                copy.flip();
                stream.offer(copy, new ReleaseCallback(bufs, copy));
            }

            @Override
            public void onSuccess(Response response) {
                if (buffered) {
                    try {
                        complete();
                    } catch (Exception e) {
                        onFailure(response, e);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            private void complete() {
                completing.set(true);
                if (buffered) {
                    cr.bufferEntity();
                }
                // TODO: dangerous cast, see javadoc!
                complete(extractor == null ? (T) cr : extractor.extractResult(cr));
            }

            @Override
            public void onFailure(Response response, Throwable failure) {
                failed(failure);
            }

            @Override
            public void onComplete(Result result) {
                try {
                    if (extractor != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
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
        if (timeoutMs > 0) {
            request.timeout(timeoutMs, TimeUnit.MILLISECONDS);
        }
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

    private static RuntimeException clientException(Throwable ex, javax.ws.rs.core.Response clientResponse) {
        RuntimeException ret;
        if (ex == null) {
            final NullPointerException e = new NullPointerException();
            e.fillInStackTrace();
            ret = new ProcessingException(e);
        }
        else if (ex instanceof WebApplicationException) {
            ret = (WebApplicationException) ex;
        }
        else if (ex instanceof ProcessingException) {
            ret = (ProcessingException) ex;
        }
        else if (clientResponse != null) {
            ret = new ResponseProcessingException(clientResponse, ex);
        }
        else {
            ret = new ProcessingException(ex);
        }
        ret.fillInStackTrace();
        return ret;
    }

    static class RequestFuture<T> extends CompletableFuture<T> {
        private final Request request;

        RequestFuture(Request request) {
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
