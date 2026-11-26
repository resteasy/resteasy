/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engines.config.HttpClientConfigurationOptions;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.CompletableFutures;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.spi.EntityOutputStream;
import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * An HTTP client engine backed by the JDK's {@link HttpClient}. One {@code HttpClient} is shared by every request, so
 * connections are pooled, and it is shut down when the engine is closed.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class JavaClientHttpEngine implements ClientHttpEngine, AsyncClientHttpEngine {
    private static final Collection<String> NO_BODY_REQUEST_METHODS = List.of(
            "CONNECT",
            "GET",
            "HEAD");
    private static final Collection<String> NO_BODY_RESPONSE_METHODS = List.of(
            "HEAD"
    //"TRACE" the spec seems to allow TRACE methods to have a response body
    );
    // Headers the HttpClient sets itself. It rejects them unless they are listed in the
    // jdk.httpclient.allowRestrictedHeaders system property.
    private static final Set<String> RESTRICTED_HEADERS = Set.of(
            "connection",
            "content-length",
            "expect",
            "host",
            "upgrade");
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final HttpClient client;
    private final ClientBuilderConfiguration configuration;
    private final HttpClient.Version version;

    /**
     * Creates an engine which uses the same {@link HttpClient} for every request.
     *
     * @param client        the client used for every request
     * @param configuration the client configuration
     */
    JavaClientHttpEngine(final HttpClient client, final ClientBuilderConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
        this.version = resolveVersion(configuration);
    }

    @Override
    public SSLContext getSslContext() {
        return configuration.sslContext();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return configuration.hostnameVerifier().orElse(null);
    }

    @Override
    public Response invoke(final Invocation request) {
        if (closed.get()) {
            throw LogMessages.LOGGER.clientIsClosed();
        }
        final ClientInvocation clientInvocation = (ClientInvocation) request;
        try {
            final PreparedRequest prepared = createRequest(clientInvocation, client);
            final HttpResponse<InputStream> response;
            try {
                response = client.send(prepared.request(), HttpResponse.BodyHandlers.ofInputStream());
            } finally {
                prepared.cleanup();
            }
            return createResponse(clientInvocation, response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw LogMessages.LOGGER.unableToInvokeRequest(e, e.toString());
        } catch (Exception e) {
            LogMessages.LOGGER.clientSendProcessingFailure(e);
            if (e instanceof ProcessingException) {
                throw ((ProcessingException) e);
            }
            final Throwable cause = readTimeoutCause(e);
            throw LogMessages.LOGGER.unableToInvokeRequest(cause, cause.toString());
        }
    }

    @Override
    public boolean isFollowRedirects() {
        return configuration.isFollowRedirects();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            client.shutdownNow();
        }
    }

    @Override
    public <T> Future<T> submit(final ClientInvocation request, final boolean buffered, final InvocationCallback<T> callback,
            final ResultExtractor<T> extractor) {
        // Records the response the result is extracted from, so it can be closed after the callback returns
        final AtomicReference<ClientResponse> underlying = new AtomicReference<>();
        final CompletableFuture<T> future = submit(request, buffered, (response) -> {
            underlying.set(response);
            return extractor.extractResult(response);
        });
        return CompletableFutures.cancelWith(future
                .whenComplete((response, error) -> {
                    if (callback != null) {
                        try {
                            if (error != null) {
                                callback.failed(CompletableFutures.unwrap(error));
                            } else {
                                callback.completed(response);
                            }
                        } catch (Throwable t) {
                            LogMessages.LOGGER.debug("Ignoring exception thrown within InvocationCallback", t);
                        } finally {
                            // The runtime closes the underlying Response after the InvocationCallback method returns
                            final ClientResponse clientResponse = underlying.get();
                            if (clientResponse != null) {
                                clientResponse.close();
                            }
                        }
                    }
                }), future);
    }

    @Override
    public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
            final ResultExtractor<T> extractor) {
        return submit(request, buffered, extractor, null);
    }

    @Override
    public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
            final ResultExtractor<T> extractor, final ExecutorService executorService) {
        if (closed.get()) {
            return CompletableFuture.failedFuture(LogMessages.LOGGER.clientIsClosed());
        }
        PreparedRequest prepared = null;
        try {
            prepared = createRequest(request, client);
            final PreparedRequest current = prepared;
            final HttpResponse.BodyHandler<InputStream> handler;
            if (buffered) {
                handler = HttpResponse.BodyHandlers.buffering(HttpResponse.BodyHandlers.ofInputStream(),
                        configuration.responseBufferSize());
            } else {
                handler = HttpResponse.BodyHandlers.ofInputStream();
            }
            final CompletableFuture<HttpResponse<InputStream>> sendFuture = client.sendAsync(prepared.request(), handler);
            final CompletableFuture<T> result = new CompletableFuture<>();
            // The response filters and readers run on another thread. Capture the thread contexts of the calling thread,
            // so contextual data is available while the response is processed.
            final Callable<T> extraction = ContextualExecutors
                    .callable(() -> extractor.extractResult(createResponse(request, sendFuture.join())));
            // The HttpClient uses its own executor for I/O. The response is processed on the client executor, so user
            // code, such as filters, readers and callbacks, does not run on, or block, the I/O threads of the HttpClient.
            final ExecutorService executor = executorService == null ? configuration.executorService().orElse(null)
                    : executorService;
            sendFuture.whenComplete((response, error) -> {
                // The request, including all redirects, has completed, so the entity is no longer needed
                current.cleanup();
                if (error != null) {
                    result.completeExceptionally(toProcessingException(error));
                } else if (executor == null) {
                    process(response, extraction, result);
                } else {
                    try {
                        executor.execute(() -> process(response, extraction, result));
                    } catch (RejectedExecutionException e) {
                        // The response is not processed, so the body is closed here
                        closeBody(response);
                        result.completeExceptionally(e);
                    }
                }
            });
            return CompletableFutures.cancelWith(result, sendFuture);
        } catch (Throwable t) {
            if (prepared != null) {
                prepared.cleanup();
            }
            return CompletableFuture.failedFuture(t);
        }
    }

    private PreparedRequest createRequest(final ClientInvocation clientInvocation, final HttpClient client)
            throws IOException {
        final ClientEntityOutputStream entity = clientInvocation.getEntity() == null ? null : new ClientEntityOutputStream();
        try {
            return new PreparedRequest(buildRequest(clientInvocation, client, entity), entity);
        } catch (IOException | RuntimeException e) {
            // The entity may have been written to a file before the failure
            if (entity != null) {
                entity.deleteFile();
            }
            throw e;
        }
    }

    private HttpRequest buildRequest(final ClientInvocation clientInvocation, final HttpClient client,
            final ClientEntityOutputStream out) throws IOException {
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(clientInvocation.getUri());

        final long readTimeout = configuration.readTimeout(TimeUnit.MILLISECONDS);
        if (readTimeout > 0) {
            requestBuilder.timeout(Duration.ofMillis(readTimeout));
        }
        final String method = clientInvocation.getMethod();
        if (clientInvocation.getEntity() != null && NO_BODY_REQUEST_METHODS.contains(method.toUpperCase(Locale.ROOT))) {
            throw LogMessages.LOGGER.bodyNotAllowed(method);
        }
        if ("GET".equals(method)) {
            // Without a body publisher, the HttpClient sends neither a Content-Length nor a Transfer-Encoding header
            requestBuilder.GET();
        } else {
            if (clientInvocation.getEntity() != null) {
                // A chunked request has a body length of -1. The HttpClient then uses chunked transfer encoding for
                // HTTP/1.1 and streams the body in DATA frames for HTTP/2, which does not allow Transfer-Encoding.
                try (out) {
                    clientInvocation.getDelegatingOutputStream().setDelegate(out);
                    clientInvocation.writeRequestBody(clientInvocation.getEntityStream());
                }
                requestBuilder.method(method, out.toPublisher(clientInvocation.isChunked()));
            } else if ("DELETE".equals(method)) {
                requestBuilder.DELETE();
            } else if ("HEAD".equals(method)) {
                requestBuilder.HEAD();
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }
        }
        // Add the headers
        final MultivaluedMap<String, String> headers = clientInvocation.getHeaders().asMap();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String name = entry.getKey();
            final boolean restricted = RESTRICTED_HEADERS.contains(name.toLowerCase(Locale.ROOT));
            for (String value : entry.getValue()) {
                if (value == null) {
                    continue;
                }
                if (restricted) {
                    addRestrictedHeader(requestBuilder, name, value);
                } else {
                    requestBuilder.header(name, value);
                }
            }
        }
        // Without an explicit version, cleartext requests use HTTP/1.1, so the client does not attempt an h2c upgrade.
        // HTTPS requests use the version of the client, and HTTP/2 is negotiated with ALPN.
        final boolean cleartext = "http".equalsIgnoreCase(clientInvocation.getUri().getScheme());
        if (version != null) {
            requestBuilder.version(version);
        } else if (cleartext) {
            requestBuilder.version(HttpClient.Version.HTTP_1_1);
        }
        return requestBuilder.build();
    }

    private ClientResponse createResponse(final ClientInvocation clientInvocation,
            final HttpResponse<InputStream> response) {
        final ClientResponse clientResponse = new ClientResponse(clientInvocation.getClientConfiguration(),
                clientInvocation.getTracingLogger()) {

            @Override
            public void releaseConnection(final boolean consumeInputStream) throws IOException {
                // getInputStream() returns null once the response is closed, which is when an unread body still
                // needs to be released
                final InputStream in = is != null ? is : body();
                if (in != null) {
                    // Drain the request if requested up to 64KB
                    if (consumeInputStream) {
                        in.readNBytes(64 * 1024);
                    }
                    in.close();
                }
            }

            @Override
            protected void setInputStream(final InputStream is) {
                //super.setInputStream(is);
                this.is = is;
                resetEntity();
            }

            @Override
            protected InputStream getInputStream() {
                if (is != null || isClosed()) {
                    return is;
                }
                return body();
            }

            private InputStream body() {
                if (NO_BODY_RESPONSE_METHODS.contains(response.request().method().toUpperCase(Locale.ROOT))) {
                    return null;
                }
                return this.is = response.body();
            }
        };
        clientResponse.setProperties(clientInvocation.getMutableProperties());
        final Response.Status status = Response.Status.fromStatusCode(response.statusCode());
        if (status == null) {
            clientResponse.setStatus(response.statusCode());
        } else {
            clientResponse.setStatus(status.getStatusCode());
            clientResponse.setReasonPhrase(status.getReasonPhrase());
        }
        clientResponse.setHeaders(extractHeaders(response));
        clientResponse.setClientConfiguration(clientInvocation.getClientConfiguration());
        return clientResponse;
    }

    private static CaseInsensitiveMap<String> extractHeaders(final HttpResponse<?> response) {
        final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<>();
        response.headers().map().forEach((name, values) -> {
            // HTTP/2 pseudo-headers, such as :status, are not response headers
            if (name.isEmpty() || name.charAt(0) == ':') {
                return;
            }
            for (String value : values) {
                headers.add(name, value);
            }
        });
        return headers;
    }

    private static Throwable readTimeoutCause(final Throwable cause) {
        // ClientBuilder.readTimeout() requires a TimeoutException as the cause of the ProcessingException. A connect
        // timeout is also an HttpTimeoutException, but it is not a read timeout.
        if (cause instanceof HttpTimeoutException && !(cause instanceof HttpConnectTimeoutException)) {
            final TimeoutException timeout = new TimeoutException(cause.getMessage());
            timeout.initCause(cause);
            return timeout;
        }
        return cause;
    }

    private static void addRestrictedHeader(final HttpRequest.Builder requestBuilder, final String name,
            final String value) {
        if ("expect".equalsIgnoreCase(name) && "100-continue".equalsIgnoreCase(value.trim())) {
            requestBuilder.expectContinue(true);
            return;
        }
        try {
            requestBuilder.header(name, value);
        } catch (IllegalArgumentException e) {
            LogMessages.LOGGER.debugf("Ignoring the %s header. The HttpClient sets it, unless it is listed in the "
                    + "jdk.httpclient.allowRestrictedHeaders system property.", name);
        }
    }

    /**
     * Processes a received response and completes the result. Once the response is received, only this method uses
     * the body, so the body is never closed while another thread reads it.
     */
    private static <T> void process(final HttpResponse<InputStream> response, final Callable<T> extraction,
            final CompletableFuture<T> result) {
        if (result.isDone()) {
            // The result was cancelled before the response was processed
            closeBody(response);
            return;
        }
        final T value;
        try {
            value = extraction.call();
        } catch (Throwable t) {
            result.completeExceptionally(t);
            return;
        }
        if (!result.complete(value)) {
            // The result was cancelled while the response was processed, so the value is not used. A Response or a
            // stream, such as an InputStream, is closed here. Other values were read and closed by the extraction.
            if (value instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    LogMessages.LOGGER.debug("Failed to close the unused result", e);
                }
            }
        }
    }

    private static ProcessingException toProcessingException(final Throwable error) {
        Throwable cause = error;
        if (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        cause = readTimeoutCause(cause);
        return LogMessages.LOGGER.unableToInvokeRequest(cause, cause.toString());
    }

    private static void closeBody(final HttpResponse<InputStream> response) {
        try {
            response.body().close();
        } catch (IOException e) {
            LogMessages.LOGGER.debug("Failed to close the response body", e);
        }
    }

    private static HttpClient.Version resolveVersion(final ClientBuilderConfiguration configuration) {
        String versionValue = null;
        if (configuration.configuration().hasProperty(HttpClientConfigurationOptions.HTTP_VERSION)) {
            final Object value = configuration.configuration().getProperty(HttpClientConfigurationOptions.HTTP_VERSION);
            if (value instanceof HttpClient.Version) {
                return (HttpClient.Version) value;
            } else if (value != null) {
                versionValue = String.valueOf(value);
            }
        } else {
            versionValue = ConfigurationFactory.getInstance().getConfiguration()
                    .getOptionalValue(HttpClientConfigurationOptions.HTTP_VERSION, String.class)
                    .orElse(null);
        }

        if (versionValue != null) {
            try {
                return HttpClient.Version.valueOf(versionValue.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignore) {
                LogMessages.LOGGER.invalidVersion(versionValue);
            }
        }
        return null;
    }

    private static class ClientEntityOutputStream extends EntityOutputStream {

        /**
         * Returns a publisher for the entity which the {@link HttpClient} can subscribe to more than once, so a
         * redirect can send the entity again. If the entity was written to a file, the file is not deleted when it
         * is read. Use {@link #deleteFile()} once the request has completed.
         *
         * @param chunked {@code true} if the request uses chunked transfer encoding
         *
         * @return the publisher for the entity
         *
         * @throws IOException if the file of the entity cannot be found
         */
        HttpRequest.BodyPublisher toPublisher(final boolean chunked) throws IOException {
            if (!isClosed()) {
                throw Messages.MESSAGES.streamNotClosed(this);
            }
            checkExported(Messages.MESSAGES.alreadyExported());
            contentLock.lock();
            try {
                final Path file = getFile();
                final HttpRequest.BodyPublisher publisher = file == null
                        ? HttpRequest.BodyPublishers.ofByteArray(getAndClearMemory())
                        : HttpRequest.BodyPublishers.ofFile(file);
                // A chunked request has no Content-Length, so the length must be -1. The
                // HttpRequest.BodyPublishers.fromPublisher() does not allow a length of -1.
                return chunked ? new DelegateBodyPublisher(publisher, -1) : publisher;
            } finally {
                contentLock.unlock();
            }
        }

        /**
         * Deletes the file the entity was written to, if any.
         */
        void deleteFile() {
            contentLock.lock();
            try {
                final Path file = getFile();
                if (file != null) {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        // On some platforms, such as Windows, a file which is still open cannot be deleted
                        LogMessages.LOGGER.debugf(e, "Failed to delete the request entity file %s", file);
                    }
                }
            } finally {
                contentLock.unlock();
            }
        }
    }

    private static class DelegateBodyPublisher implements HttpRequest.BodyPublisher {
        private final HttpRequest.BodyPublisher delegate;
        private final long len;

        private DelegateBodyPublisher(final HttpRequest.BodyPublisher delegate, final long len) {
            this.delegate = delegate;
            this.len = len;
        }

        @Override
        public long contentLength() {
            return len;
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
            delegate.subscribe(subscriber);
        }
    }

    /**
     * A request and its entity. The entity is {@code null} if the request has no entity.
     */
    private record PreparedRequest(HttpRequest request, ClientEntityOutputStream entity) {

        /**
         * Deletes the file of the entity, if any. Invoke once the request has completed, including all redirects, or
         * has failed.
         */
        void cleanup() {
            if (entity != null) {
                entity.deleteFile();
            }
        }
    }
}
