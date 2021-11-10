package org.jboss.resteasy.client.jaxrs.engines.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.FinalizedClientResponse;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VertxClientHttpEngine implements AsyncClientHttpEngine {

    /**
     * Client config property to set when a request timeout is needed.
     */
    public static final String REQUEST_TIMEOUT_MS = Vertx.class + "$RequestTimeout";

    private final Vertx vertx;
    private final HttpClient httpClient;

    /**
     * Empty constructor that creates a new vertx and http client with defaults.
     */
    public VertxClientHttpEngine() {
        this.vertx = Vertx.vertx();
        this.httpClient = vertx.createHttpClient();
    }


    /**
     * Constructor that creates a new http client from vertx with default options.
     * @param vertx vertx instance.
     */
    public VertxClientHttpEngine(final Vertx vertx) {
        this(vertx, new HttpClientOptions());
    }

    /**
     * Constructor that creates a new http client from vertx with given options.
     * @param vertx vertx instance.
     * @param options http vertx options.
     */
    public VertxClientHttpEngine(final Vertx vertx, final HttpClientOptions options) {
        this.vertx = vertx;
        this.httpClient = vertx.createHttpClient(options);
    }

    /**
     * Constructor that creates uses a vertx loop and httpclient.
     * @param vertx vertx instance.
     * @param client configured http client.
     */
    public VertxClientHttpEngine(final Vertx vertx, final HttpClient client) {
        this.vertx = vertx;
        this.httpClient = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(final ClientInvocation request,
                                final boolean buffered,
                                final InvocationCallback<T> callback,
                                final ResultExtractor<T> extractor) {
        CompletableFuture<T> future = submit(request)
                .thenCompose(response -> vertx.<T>executeBlocking(promise -> {
                    try {
                        T result = extractor.extractResult(response);
                        promise.complete(result);
                    } catch (Exception e) {
                        promise.fail(e);
                    }
                }).toCompletionStage().toCompletableFuture());
        if (callback != null) {
            future = future.whenComplete((response, throwable) -> {
                if (throwable != null) {
                    callback.failed(throwable);
                } else {
                    callback.completed(response);
                }
            });
        }
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> submit(final ClientInvocation request,
                                           final boolean buffered,
                                           final ResultExtractor<T> extractor,
                                           final ExecutorService executorService) {
        CompletableFuture<ClientResponse> future = submit(request);
        if(executorService != null) {
            return future.thenCompose(response -> CompletableFuture
                    .supplyAsync(() -> extractor.extractResult(response), executorService));
        }else {
            return future.thenCompose(response -> vertx.<T>executeBlocking(promise -> {
                try {
                    T result = extractor.extractResult(response);
                    promise.complete(result);
                } catch (Exception e) {
                    promise.fail(e);
                }
            }).toCompletionStage().toCompletableFuture());
        }
    }

    /**
     * Perform jax-rs request using vertx http client.
     * @param request request.
     * @return future response.
     */
    private CompletableFuture<ClientResponse> submit(final ClientInvocation request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        Object entity = request.getEntity();
        Buffer body;
        if (entity != null) {
            body = Buffer.buffer(requestContent(request));
        } else {
            body = null;
        }

        RequestOptions options = new RequestOptions();
        options.setMethod(method);
        MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        request.getHeaders().asMap().forEach(headers::add);
        options.setHeaders(headers);
        if (body != null) {
            headers.set(HttpHeaders.CONTENT_LENGTH, "" + body.length());
        }

        if(!headers.contains(HttpHeaders.USER_AGENT)) {
            options.addHeader(HttpHeaders.USER_AGENT.toString(), "Vertx");
        }

        URI uri = request.getUri();
        options.setHost(uri.getHost());


        if (-1 == uri.getPort()) {
            if ("http".equals(uri.getScheme())) {
                options.setPort(80);
            } else if ("https".equals(uri.getScheme())) {
                options.setPort(443);
            }
        } else {
            options.setPort(uri.getPort());
        }

        if(uri.getRawQuery() != null && !uri.getRawQuery().trim().isEmpty()) {
            options.setURI(String.format("%s?%s", uri.getRawPath(), uri.getRawQuery()));
        }else {
            options.setURI(uri.getRawPath());
        }


        Object timeout = request.getConfiguration().getProperty(REQUEST_TIMEOUT_MS);
        if (timeout != null) {
            long timeoutMs = unwrapTimeout(timeout);
            if (timeoutMs > 0) {
                options.setTimeout(timeoutMs);
            }
        }

        final CompletableFuture<ClientResponse> futureResponse = new CompletableFuture<>();
        httpClient.request(options)
                .map(httpClientRequest -> {
                    final Handler<AsyncResult<HttpClientResponse>> handler = event -> {
                        if (event.succeeded()) {
                            final HttpClientResponse response = event.result();
                            response.pause();
                            futureResponse.complete(toRestEasyResponse(request.getClientConfiguration(), response));
                            response.resume();
                        } else {
                            futureResponse.completeExceptionally(event.cause());
                        }
                    };
                    if (body != null) {
                        httpClientRequest.send(body, handler);
                    } else {
                        httpClientRequest.send(handler);
                    }
                    return null;
                });
        return futureResponse;
    }

    private long unwrapTimeout(final Object timeout) {
        if (timeout instanceof Duration) {
            return ((Duration) timeout).toMillis();
        } else if (timeout instanceof Number) {
            return ((Number) timeout).longValue();
        } else if (timeout != null) {
            return Long.parseLong(timeout.toString());
        } else {
            return -1L;
        }
    }

    @Override
    public SSLContext getSslContext() {
        // Vertx does not allow to access the ssl-context from HttpClient API.
        throw new UnsupportedOperationException();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        // Vertx does not support HostnameVerifier API.
        throw new UnsupportedOperationException();
    }

    @Override
    public Response invoke(Invocation request) {
        final Future<ClientResponse> future = submit((ClientInvocation) request);

        try {
            return future.get();
        } catch (InterruptedException e) {
            future.cancel(true);
            throw clientException(e, null);
        } catch (ExecutionException e) {
            throw clientException(e.getCause(), null);
        }
    }

    @Override
    public void close() {
        if (vertx != null) {
            vertx.close();
        } else {
            httpClient.close();
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

    private static byte[] requestContent(ClientInvocation request) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        request.getDelegatingOutputStream().setDelegate(baos);
        try {
            request.writeRequestBody(request.getEntityStream());
            baos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write the request body!", e);
        }
    }

    private ClientResponse toRestEasyResponse(ClientConfiguration clientConfiguration,
                                              HttpClientResponse clientResponse) {


        InputStreamAdapter adapter = new InputStreamAdapter(clientResponse, 4 * 1024);

        class RestEasyClientResponse extends FinalizedClientResponse {

            private InputStream is;

            private RestEasyClientResponse(final ClientConfiguration configuration) {
                super(configuration, RESTEasyTracingLogger.empty());
                this.is = adapter;
            }

            @Override
            protected InputStream getInputStream() {
                return this.is;
            }

            @Override
            protected void setInputStream(InputStream inputStream) {
                this.is = inputStream;
            }

            @Override
            public void releaseConnection() throws IOException {
                this.releaseConnection(false);
            }

            @Override
            public void releaseConnection(boolean consumeInputStream) throws IOException {
                try {
                    if (is != null) {
                        if (consumeInputStream) {
                            while (is.available() > 0) {
                                is.read();
                            }
                        }
                        is.close();
                    }
                } catch (IOException e) {
                    // Swallowing because other ClientHttpEngine implementations are swallowing as well.
                    // What is better?  causing a potential leak with inputstream slowly or cause an unexpected
                    // and unhandled io error and potentially cause the service go down?
                    // log.warn("Exception while releasing the connection!", e);
                }
            }
        }
        ClientResponse restEasyClientResponse = new RestEasyClientResponse(clientConfiguration);
        restEasyClientResponse.setStatus(clientResponse.statusCode());
        CaseInsensitiveMap<String> restEasyHeaders = new CaseInsensitiveMap<>();
        clientResponse.headers().forEach(header -> restEasyHeaders.add(header.getKey(), header.getValue()));
        restEasyClientResponse.setHeaders(restEasyHeaders);
        return restEasyClientResponse;
    }
}