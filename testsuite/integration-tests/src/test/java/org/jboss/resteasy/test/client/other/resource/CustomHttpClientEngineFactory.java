/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.client.other.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * A custom {@link ClientHttpEngineFactory} for testing.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class CustomHttpClientEngineFactory implements ClientHttpEngineFactory {

    public static class CustomAsyncHttpClientEngine implements AsyncClientHttpEngine {
        private final AtomicBoolean followRedirects = new AtomicBoolean(false);
        private final ClientBuilderConfiguration configuration;
        private final CookieManager cookieManager;

        private CustomAsyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
            this.configuration = configuration;
            this.cookieManager = new CookieManager();
        }

        @Override
        public <T> Future<T> submit(final ClientInvocation request, final boolean buffered,
                final InvocationCallback<T> callback, final ResultExtractor<T> extractor) {
            return submit(request, buffered, extractor);
        }

        @Override
        public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
                final ResultExtractor<T> extractor) {
            return submit(request, buffered, extractor, null);
        }

        @Override
        public <T> CompletableFuture<T> submit(final ClientInvocation request, final boolean buffered,
                final ResultExtractor<T> extractor, final ExecutorService executorService) {
            final HttpClient client = createClient(executorService == null ? configuration.executorService()
                    .orElse(ContextualExecutors.threadPool()) : executorService);
            final HttpRequest httpRequest;
            try {
                httpRequest = createRequest(request);
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                    .thenCompose(httpResponse -> {
                        final ClientResponse response = createResponse(request, httpResponse);
                        return CompletableFuture.completedFuture(extractor.extractResult(response));
                    });
        }

        @Override
        public SSLContext getSslContext() {
            return configuration.getSSLContext();
        }

        @Override
        public HostnameVerifier getHostnameVerifier() {
            return null;
        }

        @Override
        public Response invoke(final Invocation request) {
            final ClientInvocation clientInvocation = (ClientInvocation) request;
            try {
                final CompletableFuture<Response> cf = submit(clientInvocation, false, response -> response);
                try {
                    return cf.get();
                } catch (InterruptedException e) {
                    cf.cancel(true);
                    throw new ProcessingException(e);
                } catch (ExecutionException e) {
                    // Check the cause, if it's already a ProcessingException throw it
                    final Throwable cause = e.getCause();
                    if (cause instanceof ProcessingException) {
                        throw ((ProcessingException) cause);
                    }
                    throw new ProcessingException(e);
                }
            } catch (Exception e) {
                if (e instanceof ProcessingException) {
                    throw ((ProcessingException) e);
                }
                throw new ProcessingException(e);
            }
        }

        @Override
        public boolean isFollowRedirects() {
            return followRedirects.get();
        }

        @Override
        public void setFollowRedirects(final boolean followRedirects) {
            this.followRedirects.set(followRedirects);
        }

        @Override
        public void close() {
        }

        private HttpClient createClient(final Executor executor) {
            final HttpClient.Builder builder = HttpClient.newBuilder()
                    .followRedirects(
                            configuration.isFollowRedirects() ? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER);
            if (configuration.isCookieManagementEnabled()) {
                builder.cookieHandler(cookieManager);
            }
            if (configuration.getDefaultProxyHostname() != null) {
                builder.proxy(ProxySelector.of(InetSocketAddress.createUnresolved(configuration.getDefaultProxyHostname(),
                        configuration.getDefaultProxyPort())));
            }
            final long connectionTimeout = configuration.getConnectionTimeout(TimeUnit.MILLISECONDS);
            if (connectionTimeout > 1) {
                builder.connectTimeout(Duration.ofMillis(connectionTimeout));
            }
            builder.executor(executor);
            return builder.build();
        }

        private HttpRequest createRequest(final ClientInvocation clientInvocation) throws IOException {
            final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(clientInvocation.getUri());
            final long readTimeout = configuration.getReadTimeout(TimeUnit.MILLISECONDS);
            if (readTimeout > 1) {
                requestBuilder.timeout(Duration.ofMillis(readTimeout));
            }
            final String method = clientInvocation.getMethod();
            if ("GET".equals(method)) {
                requestBuilder.method(method, HttpRequest.BodyPublishers.fromPublisher(HttpRequest.BodyPublishers.noBody()));
            } else {
                if (clientInvocation.getEntity() != null) {
                    if (clientInvocation.isChunked()) {
                        // Chunked transfer encoding only works with HTTP/1.1
                        requestBuilder.version(HttpClient.Version.HTTP_1_1)
                                .header("Transfer-Encoding", "chunked");
                    }
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        clientInvocation.getDelegatingOutputStream().setDelegate(out);
                        clientInvocation.writeRequestBody(clientInvocation.getEntityStream());
                        requestBuilder.method(method, HttpRequest.BodyPublishers.ofByteArray(out.toByteArray()));
                    }
                } else {
                    requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
                }
            }
            // Add the headers
            final MultivaluedMap<String, String> headers = clientInvocation.getHeaders().asMap();
            for (var entry : headers.entrySet()) {
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
                    }
                }

                @Override
                protected void setInputStream(final InputStream is) {
                    resetEntity();
                }

                @Override
                protected InputStream getInputStream() {
                    final InputStream is = this.is;
                    if (is != null || isClosed()) {
                        return is;
                    }
                    setInputStream(response.body());
                    return response.body();
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
                for (String value : values) {
                    headers.add(name, value);
                }
            });
            return headers;
        }
    }

    @Override
    public AsyncClientHttpEngine asyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
        return new CustomAsyncHttpClientEngine(configuration);
    }
}
