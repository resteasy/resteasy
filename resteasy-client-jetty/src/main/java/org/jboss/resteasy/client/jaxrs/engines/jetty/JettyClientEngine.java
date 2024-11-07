package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.InputStreamResponseListener;
import org.eclipse.jetty.client.OutputStreamRequestContent;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpFields;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

public class JettyClientEngine implements AsyncClientHttpEngine {

    private static final Logger LOGGER = Logger.getLogger(JettyClientEngine.class);
    private static final MediaType MULTIPART_WILDCARD = new MediaType("multipart", "*");
    private static final Class<?> MULTIPART_OUTPUT;

    static {
        // Check if the org.jboss.resteasy.plugins.providers.multipart.MultipartOutput is on the class path
        final String className = "org.jboss.resteasy.plugins.providers.multipart.MultipartOutput";
        Class<?> multipartOutput = null;
        try {
            multipartOutput = Class.forName(className, false, resolveClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.tracef(e, "Failed to load %s", className);
        }

        MULTIPART_OUTPUT = multipartOutput;
    }
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
    private final long idleTimeout;
    private final long readTimeout;

    public JettyClientEngine(final HttpClient client) {
        this(client, -1, -1);
    }

    public JettyClientEngine(final HttpClient client, final long idleTimeout, final long readTimeout) {
        if (!client.isStarted()) {
            try {
                client.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.client = client;
        this.idleTimeout = idleTimeout;
        this.readTimeout = readTimeout;
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

        // Determine if this is a multipart request
        final Object entity = invocation.getEntity();
        final boolean addBoundary = isMultipart(invocation) && canSetBoundary(entity);

        invocation.getMutableProperties().forEach(request::attribute);
        request.method(invocation.getMethod());
        request.headers(mutableHeaders -> invocation.getHeaders().asMap()
                .forEach((h, vs) -> vs.forEach(v -> {
                    String headerValue = v;
                    if (addBoundary && h.equalsIgnoreCase("content-type")) {
                        final MediaType mediaType = MediaType.valueOf(v);
                        // Set the boundary if needed
                        if (mediaType.getParameters().get("boundary") == null) {
                            headerValue = headerValue + "; boundary=" + UUID.randomUUID();
                            // Replace the MediaType on the invocation if we've added a boundary
                            invocation.getHeaders().setMediaType(MediaType.valueOf(headerValue));
                        }
                    }
                    mutableHeaders.add(h, headerValue);
                })));
        configureTimeout(request);
        if (request.getAttributes().get(FOLLOW_REDIRECTS) == Boolean.FALSE) {
            request.followRedirects(false);
        }

        if (entity != null) {
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
        } else if (readTimeout > 0L) {
            request.timeout(readTimeout, TimeUnit.MILLISECONDS);
        }

        if (idleTimeoutMs > 0) {
            request.idleTimeout(idleTimeoutMs, TimeUnit.MILLISECONDS);
        } else if (this.idleTimeout > 0L) {
            request.idleTimeout(this.idleTimeout, TimeUnit.MILLISECONDS);
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

    private static ClassLoader resolveClassLoader() {
        if (System.getSecurityManager() == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = JettyClientEngine.class.getClassLoader();
            }
            return cl == null ? ClassLoader.getSystemClassLoader() : cl;
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = JettyClientEngine.class.getClassLoader();
            }
            return cl == null ? ClassLoader.getSystemClassLoader() : cl;
        });
    }

    private static boolean isMultipart(final ClientInvocation invocation) {
        return MULTIPART_WILDCARD.isCompatible(invocation.getHeaders().getMediaType());
    }

    private static boolean canSetBoundary(final Object entity) {
        if (MULTIPART_OUTPUT != null && MULTIPART_OUTPUT.isInstance(entity)) {
            return true;
        }
        if (entity instanceof EntityPart) {
            return true;
        }
        if (entity instanceof final List<?> list) {
            // We're a list, if we're not empty check the first type to see if it's an entity part
            if (!list.isEmpty()) {
                return list.get(0) instanceof EntityPart;
            }
        }
        return false;
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
