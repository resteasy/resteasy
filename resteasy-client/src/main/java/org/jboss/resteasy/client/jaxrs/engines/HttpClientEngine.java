/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Cleaner;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResponseHeaders;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.spi.ResourceCleaner;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class HttpClientEngine implements AsyncClientHttpEngine {
    private static final Collection<String> NO_BODY_REQUEST_METHODS = List.of(
            "CONNECT",
            "GET",
            "HEAD");
    private static final Collection<String> NO_BODY_RESPONSE_METHODS = List.of(
            "HEAD"
    //"TRACE" the spec seems to allow TRACE methods to have a response body
    );

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final ResteasyClientBuilder resteasyClientBuilder;
    private final Executor defaultExecutor;
    private final BlockingQueue<AutoCloseable> closeables = new LinkedBlockingQueue<>();
    private final CookieManager cookieManager;
    private final ClientConfigProvider clientConfigProvider;

    public HttpClientEngine(final ResteasyClientBuilder resteasyClientBuilder,
            final Executor defaultExecutor, final ClientConfigProvider clientConfigProvider) {
        this.resteasyClientBuilder = resteasyClientBuilder;
        this.defaultExecutor = defaultExecutor;
        this.cookieManager = new CookieManager();
        this.clientConfigProvider = clientConfigProvider;
    }

    @Override
    public SSLContext getSslContext() {
        return resteasyClientBuilder.getSSLContext();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return resteasyClientBuilder.getHostnameVerifier();
    }

    @Override
    public Response invoke(final Invocation request) {
        final ClientInvocation clientInvocation = (ClientInvocation) request;
        try {
            final CompletableFuture<Response> cf = submit(defaultExecutor, clientInvocation, false, response -> response);
            try {
                return cf.get();
            } catch (InterruptedException e) {
                cf.cancel(true);
                throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(e.toString()), e);
            } catch (ExecutionException e) {
                // Check the cause, if it's already a ProcessingException throw it
                final Throwable cause = e.getCause();
                if (cause instanceof ProcessingException) {
                    throw ((ProcessingException) cause);
                }
                throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(e.toString()), e);
            }
        } catch (Exception e) {
            LogMessages.LOGGER.clientSendProcessingFailure(e);
            if (e instanceof ProcessingException) {
                throw ((ProcessingException) e);
            }
            throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(e.toString()), e);
        }
    }

    @Override
    public boolean isFollowRedirects() {
        return resteasyClientBuilder.isFollowRedirects();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            AutoCloseable closeable;
            while ((closeable = closeables.poll()) != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    LogMessages.LOGGER.tracef(e, "Failed closing %s", closeable);
                }
            }
        }
    }

    @Override
    public <T> Future<T> submit(final ClientInvocation request, final boolean buffered,
            final InvocationCallback<T> callback,
            final ResultExtractor<T> extractor) {
        return submit(request, buffered, extractor)
                .whenComplete((response, error) -> {
                    if (callback != null) {
                        if (error != null) {
                            callback.failed(error);
                        } else {
                            try {
                                callback.completed(response);
                            } catch (Throwable t) {
                                LogMessages.LOGGER.exceptionIgnored(t);
                            } finally {
                                // If this is a response then, it must be closed by the runtime as defined in
                                // InvocationCallback.completed()
                                if (response instanceof Response) {
                                    ((Response) response).close();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
            final ResultExtractor<T> extractor) {
        return submit(request, buffered, extractor, null);
    }

    @Override
    public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
            final ResultExtractor<T> extractor,
            final ExecutorService executorService) {
        try {
            return submit(executorService, request, buffered, extractor);
        } catch (Throwable t) {
            return CompletableFuture.failedFuture(t);
        }
    }

    private <T> CompletableFuture<T> submit(final Executor executor, final ClientInvocation request,
            final boolean buffered,
            final ResultExtractor<T> extractor) {
        if (closed.get()) {
            return CompletableFuture.failedFuture(new ProcessingException(Messages.MESSAGES.clientIsClosed()));
        }
        try {
            final HttpRequest httpRequest = createRequest(request);
            final HttpResponse.BodyHandler<InputStream> handler;
            if (buffered) {
                handler = new TrackingBodyHandler(
                        HttpResponse.BodyHandlers.buffering(HttpResponse.BodyHandlers.ofInputStream(), Integer.MAX_VALUE));
            } else {
                handler = new TrackingBodyHandler(HttpResponse.BodyHandlers.ofInputStream());
            }
            final BiFunction<HttpResponse<InputStream>, Throwable, HttpResponse<InputStream>> responseHandler = (response,
                    error) -> {
                if (error != null) {
                    Throwable cause = error;
                    if (cause instanceof CompletionException && cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    // Close the results before we throw the error
                    try {
                        response.body().close();
                    } catch (Exception ignore) {
                    }
                    throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(cause.toString()), cause);
                }
                return response;
            };
            return queue(createClient(request, executor, request.getUri()), httpRequest, handler)
                    .handle(responseHandler)
                    .thenApply((response) -> extractor.extractResult(createResponse(request, response)));
        } catch (Throwable t) {
            return CompletableFuture.failedFuture(t);
        }
    }

    private HttpClient createClient(final ClientInvocation clientInvocation, final Executor executor, final URI uri)
            throws Exception {
        final HttpClient.Builder builder = clientBuilder(clientInvocation)
                .followRedirects(
                        resteasyClientBuilder.isFollowRedirects() ? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER);
        if (resteasyClientBuilder.getHostnameVerifier() != null || resteasyClientBuilder
                .getHostnameVerification() != ResteasyClientBuilder.HostnameVerificationPolicy.DEFAULT) {
            throw Messages.MESSAGES.hostnameVerifierSet();
        }
        SSLContext sslContext = null;
        if (resteasyClientBuilder.isTrustManagerDisabled()) {
            sslContext = resolveSslContext(resteasyClientBuilder.getConfiguration());
            sslContext.init(null, new TrustManager[] { new PassthroughTrustManager() }, null);
        } else if (resteasyClientBuilder.getSSLContext() != null) {
            sslContext = resteasyClientBuilder.getSSLContext();
        } else if (resteasyClientBuilder.getKeyStore() != null || resteasyClientBuilder.getTrustStore() != null) {
            final KeyStore keyStore = resteasyClientBuilder.getKeyStore();
            final KeyStore trustStore = resteasyClientBuilder.getTrustStore();
            sslContext = resolveSslContext(resteasyClientBuilder.getConfiguration());
            final KeyManager[] keyManagers;
            if (keyStore != null) {
                keyManagers = new KeyManager[] {
                        HttpSslUtils.getKeyManager(keyStore, resteasyClientBuilder.getKeyStorePassword()) };
            } else {
                keyManagers = HttpSslUtils.getKeyManagers(null, resteasyClientBuilder.getKeyStorePassword());
            }
            final TrustManager[] trustManagers;
            if (trustStore != null) {
                final X509TrustManager trustManager;
                if (resteasyClientBuilder.isTrustSelfSignedCertificates()) {
                    trustManager = new TrustSelfSignedTrustManager(HttpSslUtils.getTrustManager(trustStore));
                    trustManagers = new TrustManager[] { trustManager };
                } else {
                    trustManagers = HttpSslUtils.getTrustManagers(trustStore);
                }
            } else {
                trustManagers = HttpSslUtils.getTrustManagers(null);
            }
            sslContext.init(keyManagers, trustManagers, null);
        } else if (clientConfigProvider != null) {
            sslContext = clientConfigProvider.getSSLContext(uri);
        }
        if (sslContext != null) {
            if (!resteasyClientBuilder.getSniHostNames().isEmpty()) {
                final SSLParameters sslParameters = new SSLParameters();
                sslParameters.setServerNames(resteasyClientBuilder.getSniHostNames()
                        .stream()
                        .map(SNIHostName::new)
                        .collect(Collectors.toList()));
                builder.sslParameters(sslParameters);
            }
            builder.sslContext(sslContext);
        }
        if (resteasyClientBuilder.isCookieManagementEnabled()) {
            builder.cookieHandler(cookieManager);
        }
        if (resteasyClientBuilder.getDefaultProxyHostname() != null) {
            builder.proxy(ProxySelector.of(InetSocketAddress.createUnresolved(resteasyClientBuilder.getDefaultProxyHostname(),
                    resteasyClientBuilder.getDefaultProxyPort())));
        }
        final long connectionTimeout = resteasyClientBuilder.getConnectionCheckoutTimeout(TimeUnit.MILLISECONDS);
        if (connectionTimeout > 1) {
            builder.connectTimeout(Duration.ofMillis(connectionTimeout));
        }
        final Object httpVersion = resteasyClientBuilder.getConfiguration()
                .getProperty(Options.HTTP_CLIENT_VERSION.name());
        final HttpClient.Version version;
        if (httpVersion == null) {
            version = getOptionValue(Options.HTTP_CLIENT_VERSION);
        } else {
            if (httpVersion instanceof HttpClient.Version) {
                version = (HttpClient.Version) httpVersion;
            } else if (httpVersion instanceof String) {
                version = HttpClient.Version.valueOf((String) httpVersion);
            } else {
                version = getOptionValue(Options.HTTP_CLIENT_VERSION);
                LogMessages.LOGGER.invalidVersion(httpVersion, version);
            }
        }
        builder.version(version);
        if (executor != null) {
            builder.executor(ContextualExecutors.wrap(executor));
        } else {
            builder.executor(defaultExecutor);
        }
        return builder.build();
    }

    private CompletableFuture<HttpResponse<InputStream>> queue(final HttpClient client, final HttpRequest httpRequest,
            final HttpResponse.BodyHandler<InputStream> handler) {
        // TODO (jrp) this needs to be validated
        final AtomicBoolean autoClosed = new AtomicBoolean(true);
        final Cleaner.Cleanable cleanable = ResourceCleaner.register(client,
                new ClientCleanupAction(closed, autoClosed, client));
        closeables.add(() -> {
            try {
                close();
            } finally {
                autoClosed.set(false);
                cleanable.clean();
            }
        });
        return client.sendAsync(httpRequest, handler);
    }

    private <T> CompletableFuture<T> queue(final CompletableFuture<T> cf) {
        final AutoCloseable closeable = () -> {
            if (!cf.isDone()) {
                cf.cancel(true);
            }
        };
        if (!cf.isDone()) {
            closeables.add(closeable);
        }
        return cf.whenComplete((value, t) -> {
            if (!closeables.remove(closeable)) {
                LogMessages.LOGGER.debugf("Failed to remove %s", closeable);
            }
        });
    }

    private HttpRequest createRequest(final ClientInvocation clientInvocation) throws IOException {
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(clientInvocation.getUri());
        final long readTimeout = resteasyClientBuilder.getReadTimeout(TimeUnit.MILLISECONDS);
        if (readTimeout > 1) {
            requestBuilder.timeout(Duration.ofMillis(readTimeout));
        }
        final String method = clientInvocation.getMethod();
        if ("GET".equals(method)) {
            // The TCK seems to require a -1 for the content-length of GET requests. We'll just satisfy that here as
            // it's likely not a big deal.
            requestBuilder.method(method, HttpRequest.BodyPublishers.fromPublisher(HttpRequest.BodyPublishers.noBody()));
        } else {
            if (clientInvocation.getEntity() != null) {
                if (NO_BODY_REQUEST_METHODS.contains(method.toUpperCase(Locale.ROOT))) {
                    throw Messages.MESSAGES.bodyNotAllowed(method);
                }
                final ClientEntityOutputStream out = new ClientEntityOutputStream(
                        () -> "resteasy-" + clientInvocation + "-" + method);
                if (clientInvocation.isChunked()) {
                    // Chunked transfer encoding only works with HTTP/1.1
                    requestBuilder.version(HttpClient.Version.HTTP_1_1)
                            .header("Transfer-Encoding", "chunked");
                }
                // Checkstyle chokes on this and throws an NPE. Once this is fixed we should prefer the try-with-resources
                //try (out) {
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    clientInvocation.getDelegatingOutputStream().setDelegate(out);
                    clientInvocation.writeRequestBody(clientInvocation.getEntityStream());
                } finally {
                    out.close();
                }
                // If this is not a chunked request, we want to wrap the publisher with the real size. Otherwise, we
                // need the size of -1 so the content-length is not defined in the header
                requestBuilder.method(method, out.toPublisher(!clientInvocation.isChunked()));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }
        }
        // Add the headers
        final MultivaluedMap<String, String> headers = clientInvocation.getHeaders().asMap();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                if (value != null) {
                    requestBuilder.header(entry.getKey(), value);
                }
            }
        }
        return requestBuilder.build();
    }

    private ClientResponse createResponse(final ClientInvocation clientInvocation,
            final HttpResponse<InputStream> response) {
        final ClientResponse clientResponse = new ClientResponse(clientInvocation.getClientConfiguration(),
                clientInvocation.getTracingLogger()) {

            @Override
            public void releaseConnection(final boolean consumeInputStream) throws IOException {
                final InputStream in = getInputStream();
                if (in != null) {
                    in.close();
                    super.setInputStream(null);
                }
            }

            @Override
            protected void setInputStream(final InputStream is) {
                super.setInputStream(is);
                resetEntity();
            }

            @Override
            protected InputStream getInputStream() {
                final InputStream is = this.is;
                if (is != null || isClosed()) {
                    return is;
                }
                if (NO_BODY_RESPONSE_METHODS.contains(response.request().method().toUpperCase(Locale.ROOT))) {
                    return null;
                }
                final InputStream body = new TrackedInputStream(response.body());
                super.setInputStream(body);
                return body;
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

    private HttpClient.Builder clientBuilder(final ClientInvocation clientInvocation) {
        final ClientConfiguration clientConfiguration = clientInvocation.getClientConfiguration();
        if (clientConfiguration.getConfiguration().hasProperty(Options.HTTP_CLIENT_BUILDER.name())) {
            return (HttpClient.Builder) clientConfiguration.getConfiguration()
                    .getProperty(Options.HTTP_CLIENT_BUILDER.name());
        }
        final Configuration configuration = clientConfiguration.getConfiguration();
        if (configuration.hasProperty(Options.HTTP_CLIENT_BUILDER.name())) {
            return (HttpClient.Builder) configuration.getProperty(Options.HTTP_CLIENT_BUILDER.name());
        }
        return getOptionValue(Options.HTTP_CLIENT_BUILDER);
    }

    private static CaseInsensitiveMap<String> extractHeaders(final HttpResponse<?> response) {
        final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<>();
        response.headers().map().forEach((name, values) -> {
            for (String value : values) {
                headers.add(ResponseHeaders.lowerToDefault(name), value);
            }
        });
        return headers;
    }

    private static SSLContext resolveSslContext(final Configuration configuration) throws NoSuchAlgorithmException {
        // https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#sslcontext-algorithms
        final Object protocolObject = configuration.getProperty(Options.CLIENT_SSL_CONTEXT_PROTOCOL.name());
        final String protocol;
        if (protocolObject == null) {
            protocol = getOptionValue(Options.CLIENT_SSL_CONTEXT_PROTOCOL);
        } else {
            if (protocolObject instanceof String) {
                protocol = (String) protocolObject;
            } else {
                protocol = getOptionValue(Options.CLIENT_SSL_CONTEXT_PROTOCOL);
                LogMessages.LOGGER.invalidProtocol(protocolObject, protocol);
            }
        }
        return SSLContext.getInstance(protocol);
    }

    private static <T> T getOptionValue(final Options<T> option) {
        if (System.getSecurityManager() == null) {
            return option.getValue();
        }
        return AccessController.doPrivileged((PrivilegedAction<T>) option::getValue);
    }

    private class TrackedInputStream extends InputStream {
        private final InputStream delegate;

        private TrackedInputStream(final InputStream delegate) {
            this.delegate = delegate;
            closeables.add(this);
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(final int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            if (!closeables.remove(this)) {
                LogMessages.LOGGER.debugf("Unable to remove InputStream %s from closeables %s", this, closeables);
            }
            delegate.close();
        }

        @Override
        public void mark(final int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public long transferTo(final OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }
    }

    private class TrackingBodyHandler implements HttpResponse.BodyHandler<InputStream> {
        private final HttpResponse.BodyHandler<InputStream> delegate;

        private TrackingBodyHandler(final HttpResponse.BodyHandler<InputStream> delegate) {
            this.delegate = delegate;
        }

        @Override
        public HttpResponse.BodySubscriber<InputStream> apply(final HttpResponse.ResponseInfo responseInfo) {
            return new TrackedBodySubscriber(delegate.apply(responseInfo));
        }
    }

    private class TrackedBodySubscriber implements HttpResponse.BodySubscriber<InputStream>, AutoCloseable {
        private final HttpResponse.BodySubscriber<InputStream> delegate;
        private volatile Flow.Subscription subscription;

        private TrackedBodySubscriber(final HttpResponse.BodySubscriber<InputStream> delegate) {
            this.delegate = delegate;
        }

        @Override
        public CompletionStage<InputStream> getBody() {
            return delegate.getBody();
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            this.subscription = subscription;
            closeables.add(this);
            delegate.onSubscribe(subscription);
        }

        @Override
        public void onNext(final List<ByteBuffer> item) {
            delegate.onNext(item);
        }

        @Override
        public void onError(final Throwable throwable) {
            LogMessages.LOGGER.debugf(throwable, "Error occurred on subscriber %s", delegate);
            delegate.onError(throwable);
        }

        @Override
        public void onComplete() {
            if (closeables.remove(this)) {
                LogMessages.LOGGER.debugf("%s was not found in %s and therefore not removed.", this, closeables);
            }
            delegate.onComplete();
        }

        @Override
        public void close() throws Exception {
            final Flow.Subscription subscription = this.subscription;
            LogMessages.LOGGER.debugf("Closing %s and cancelling subscription %s", delegate, subscription);
            if (subscription != null) {
                subscription.cancel();
                if (!closeables.remove(this)) {
                    LogMessages.LOGGER.debugf("%s was not found in %s and therefore not removed.", this, closeables);
                }
            }
        }
    }
}
