package org.jboss.resteasy.client.jaxrs.engines;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.ref.Cleaner;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.FinalizedClientResponse;
import org.jboss.resteasy.spi.ResourceCleaner;
import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.spi.config.Threshold;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * An Apache HTTP engine for use with the new Builder Config style.
 */
public class ManualClosingApacheHttpClient43Engine implements ApacheHttpClientEngine {
    private static class CleanupAction implements Runnable {
        private final AtomicBoolean closed;
        private final AtomicBoolean autoClosed;
        private final CloseableHttpClient client;

        private CleanupAction(final AtomicBoolean closed, final AtomicBoolean autoClosed, final CloseableHttpClient client) {
            this.closed = closed;
            this.autoClosed = autoClosed;
            this.client = client;
        }

        @Override
        public void run() {
            if (closed.compareAndSet(false, true)) {
                if (client != null) {
                    if (autoClosed.get()) {
                        LogMessages.LOGGER.closingForYou(this.getClass());
                    }
                    try {
                        client.close();
                    } catch (Exception e) {
                        LogMessages.LOGGER.debugf(e, "Failed to close client %s", client);
                    }
                }
            }
        }
    }

    static final String FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY = "org.jboss.resteasy.client.jaxrs.engines.fileUploadInMemoryThreshold";

    /**
     * Used to build temp file prefix.
     */
    private static final String processId;

    static {
        try {
            processId = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws Exception {
                    return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^0-9a-zA-Z]", "");
                }

            });
        } catch (PrivilegedActionException pae) {
            throw new RuntimeException(pae);
        }

    }

    protected final HttpClient httpClient;

    protected final AtomicBoolean closed;
    private final AtomicBoolean autoClosed;
    private final Cleaner.Cleanable cleanable;

    protected final boolean allowClosingHttpClient;

    protected HttpContextProvider httpContextProvider;

    protected SSLContext sslContext;

    protected HostnameVerifier hostnameVerifier;

    protected int responseBufferSize = 8192;

    protected HttpHost defaultProxy = null;

    protected boolean chunked = false;

    protected boolean followRedirects = false;

    /**
     * For uploading File's over JAX-RS framework, this property, together with {@link #fileUploadMemoryUnit},
     * defines the maximum File size allowed in memory. If fileSize exceeds this size, it will be stored to
     * {@link #fileUploadTempFileDir}. <br>
     * <br>
     * Defaults to 1 MB
     *
     * @deprecated Use {@link #getFileUploadMemoryThreshold()} or {@link #setFileUploadMemoryThreshold(Threshold)}
     */
    @Deprecated
    protected int fileUploadInMemoryThresholdLimit = 1;

    /**
     * The unit for {@link #fileUploadInMemoryThresholdLimit}. <br>
     * <br>
     * Defaults to MB.
     *
     * @see MemoryUnit
     * @deprecated Use {@link #getFileUploadMemoryThreshold()} or {@link #setFileUploadMemoryThreshold(Threshold)}
     */
    @Deprecated
    protected MemoryUnit fileUploadMemoryUnit = MemoryUnit.MB;

    /**
     * Temp directory to write output request stream to. Any file to be uploaded has to be written out to the
     * output request stream to be sent to the service and when the File is too huge the output request stream is
     * written out to the disk rather than to memory. <br>
     * <br>
     * Defaults to JVM temp directory.
     */
    protected Path fileUploadTempFileDir = getTempDir();

    public ManualClosingApacheHttpClient43Engine() {
        this(null, null, true, null);
    }

    public ManualClosingApacheHttpClient43Engine(final HttpHost defaultProxy) {
        this(null, null, true, defaultProxy);
    }

    public ManualClosingApacheHttpClient43Engine(final HttpClient httpClient) {
        this(httpClient, null, true, null);
    }

    public ManualClosingApacheHttpClient43Engine(final HttpClient httpClient, final boolean closeHttpClient) {
        this(httpClient, null, closeHttpClient, null);
    }

    public ManualClosingApacheHttpClient43Engine(final HttpClient httpClient,
            final HttpContextProvider httpContextProvider) {
        this(httpClient, httpContextProvider, true, null);
    }

    private ManualClosingApacheHttpClient43Engine(final HttpClient httpClient,
            final HttpContextProvider httpContextProvider, final boolean closeHttpClient, final HttpHost defaultProxy) {
        this.httpClient = httpClient != null ? httpClient : createDefaultHttpClient();
        if (closeHttpClient && !(this.httpClient instanceof CloseableHttpClient)) {
            throw new IllegalArgumentException(
                    "httpClient must be a CloseableHttpClient instance in order for allowing engine to close it!");
        }
        this.httpContextProvider = httpContextProvider;
        this.allowClosingHttpClient = closeHttpClient;
        closed = new AtomicBoolean(false);
        autoClosed = new AtomicBoolean(true);
        this.cleanable = createCleanable(this, closeHttpClient, closed, autoClosed, this.httpClient);
        this.defaultProxy = defaultProxy;

        try {
            int threshold = getProperty(FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY, Integer.class, () -> 1);
            if (threshold > -1) {
                this.fileUploadInMemoryThresholdLimit = threshold;
            }
            LogMessages.LOGGER.debugf("Negative threshold, %s, specified. Using default value", threshold);
        } catch (Exception e) {
            LogMessages.LOGGER.debug("Exception caught parsing memory threshold. Using default value.", e);
        }
    }

    /**
     * Response stream is wrapped in a BufferedInputStream. Default is 8192. Value of 0 will not wrap it.
     * Value of -1 will use a SelfExpandingBufferedInputStream
     *
     * @return response buffer size
     */
    public int getResponseBufferSize() {
        return responseBufferSize;
    }

    /**
     * Response stream is wrapped in a BufferedInputStream. Default is 8192. Value of 0 will not wrap it.
     * Value of -1 will use a SelfExpandingBufferedInputStream
     *
     * @param responseBufferSize response buffer size
     */
    public void setResponseBufferSize(int responseBufferSize) {
        this.responseBufferSize = responseBufferSize;
    }

    /**
     * Based on memory unit
     *
     * @return threshold limit
     * @deprecated use {@link #getFileUploadMemoryThreshold()}
     */
    @Deprecated
    public int getFileUploadInMemoryThresholdLimit() {
        return fileUploadInMemoryThresholdLimit;
    }

    /**
     * @deprecated use {@link #setFileUploadMemoryThreshold(Threshold)}
     */
    @Deprecated
    public void setFileUploadInMemoryThresholdLimit(int fileUploadInMemoryThresholdLimit) {
        this.fileUploadInMemoryThresholdLimit = fileUploadInMemoryThresholdLimit;
    }

    /**
     * Returns the memory threshold of the amount of data to hold in memory.
     *
     * @return the memory threshold
     */
    public Threshold getFileUploadMemoryThreshold() {
        return Threshold.of(fileUploadInMemoryThresholdLimit, fileUploadMemoryUnit.toSizeUnit());
    }

    /**
     * Sets the memory threshold for the amount of content to hold in memory before it offloads to offline storage.
     *
     * @param threshold the in memory threshold
     */
    public void setFileUploadMemoryThreshold(final Threshold threshold) {
        fileUploadInMemoryThresholdLimit = (int) threshold.toBytes();
        fileUploadMemoryUnit = MemoryUnit.of(threshold.sizeUnit());
    }

    /**
     * @deprecated use {@link #getFileUploadMemoryThreshold()}
     */
    @Deprecated
    public MemoryUnit getFileUploadMemoryUnit() {
        return fileUploadMemoryUnit;
    }

    /**
     * @deprecated use {@link #setFileUploadMemoryThreshold(Threshold)}
     */
    @Deprecated
    public void setFileUploadMemoryUnit(MemoryUnit fileUploadMemoryUnit) {
        this.fileUploadMemoryUnit = fileUploadMemoryUnit;
    }

    public File getFileUploadTempFileDir() {
        return fileUploadTempFileDir.toFile();
    }

    public void setFileUploadTempFileDir(File fileUploadTempFileDir) {
        this.fileUploadTempFileDir = fileUploadTempFileDir.toPath();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public static CaseInsensitiveMap<String> extractHeaders(HttpResponse response) {
        final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();

        for (Header header : response.getAllHeaders()) {
            headers.add(header.getName(), header.getValue());
        }
        return headers;
    }

    protected InputStream createBufferedStream(InputStream is) {
        if (responseBufferSize == 0) {
            return is;
        }
        if (responseBufferSize < 0) {
            return new SelfExpandingBufferredInputStream(is);
        }
        return new BufferedInputStream(is, responseBufferSize);
    }

    @Override
    public Response invoke(Invocation inv) {
        ClientInvocation request = (ClientInvocation) inv;
        String uri = request.getUri().toString();
        final HttpRequestBase httpMethod = createHttpMethod(uri, request.getMethod());
        final HttpResponse res;
        try {
            loadHttpMethod(request, httpMethod);

            if (System.getSecurityManager() == null) {
                res = httpClient.execute(httpMethod,
                        ((httpContextProvider == null) ? null : httpContextProvider.getContext()));
            } else {
                try {
                    res = AccessController.doPrivileged(new PrivilegedExceptionAction<HttpResponse>() {
                        @Override
                        public HttpResponse run() throws Exception {
                            return httpClient.execute(httpMethod,
                                    ((httpContextProvider == null) ? null : httpContextProvider.getContext()));
                        }
                    });
                } catch (PrivilegedActionException pae) {
                    throw new RuntimeException(pae);
                }
            }
        } catch (Exception e) {
            LogMessages.LOGGER.clientSendProcessingFailure(e);
            throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(e.toString()), e);
        } finally {
            cleanUpAfterExecute(httpMethod);
        }

        ClientResponse response = new FinalizedClientResponse(request.getClientConfiguration(), request.getTracingLogger()) {
            InputStream stream;
            InputStream hc4Stream;

            @Override
            protected void setInputStream(InputStream is) {
                stream = is;
                resetEntity();
            }

            public InputStream getInputStream() {
                if (stream == null) {
                    HttpEntity entity = res.getEntity();
                    if (entity == null)
                        return null;
                    try {
                        hc4Stream = entity.getContent();
                        stream = createBufferedStream(hc4Stream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return stream;
            }

            @Override
            public void releaseConnection() throws IOException {
                releaseConnection(true);
            }

            @Override
            public void releaseConnection(boolean consumeInputStream) throws IOException {
                if (consumeInputStream) {
                    // Apache Client 4 is stupid,  You have to get the InputStream and close it if there is an entity
                    // otherwise the connection is never released.  There is, of course, no close() method on response
                    // to make this easier.
                    try {
                        // Another stupid thing...TCK is testing a specific exception from stream.close()
                        // so, we let it propagate up.
                        if (stream != null) {
                            stream.close();
                        } else {
                            InputStream is = getInputStream();
                            if (is != null) {
                                is.close();
                            }
                        }
                    } finally {
                        // just in case the input stream was entirely replaced and not wrapped, we need
                        // to close the apache client input stream.
                        if (hc4Stream != null) {
                            try {
                                hc4Stream.close();
                            } catch (IOException ignored) {

                            }
                        } else {
                            try {
                                HttpEntity entity = res.getEntity();
                                if (entity != null)
                                    entity.getContent().close();
                            } catch (IOException ignored) {
                            }

                        }

                    }
                } else if (res instanceof CloseableHttpResponse) {
                    try {
                        ((CloseableHttpResponse) res).close();
                    } catch (IOException e) {
                        LogMessages.LOGGER.warn(Messages.MESSAGES.couldNotCloseHttpResponse(), e);
                    }
                }
            }

        };
        response.setProperties(request.getMutableProperties());
        response.setStatus(res.getStatusLine().getStatusCode());
        response.setReasonPhrase(res.getStatusLine().getReasonPhrase());
        response.setHeaders(extractHeaders(res));
        response.setClientConfiguration(request.getClientConfiguration());
        response.setResolvedURI(uri);
        return response;
    }

    protected HttpRequestBase createHttpMethod(String url, String restVerb) {
        if ("GET".equals(restVerb)) {
            return new HttpGet(url);
        } else if ("POST".equals(restVerb)) {
            return new HttpPost(url);
        } else {
            final String verb = restVerb;
            return new HttpPost(url) {
                @Override
                public String getMethod() {
                    return verb;
                }
            };
        }
    }

    protected void loadHttpMethod(final ClientInvocation request, HttpRequestBase httpMethod) throws Exception {
        if (isFollowRedirects()) {
            setRedirectRequired(request, httpMethod);
        } else {
            setRedirectNotRequired(request, httpMethod);
        }

        if (request.getEntity() != null) {
            if (httpMethod instanceof HttpGet)
                throw new ProcessingException(Messages.MESSAGES.getRequestCannotHaveBody());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.getDelegatingOutputStream().setDelegate(baos);
            try {
                HttpEntity entity = buildEntity(request);
                HttpPost post = (HttpPost) httpMethod;
                commitHeaders(request, httpMethod);
                post.setEntity(entity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else // no body
        {
            commitHeaders(request, httpMethod);
        }
    }

    protected void commitHeaders(ClientInvocation request, HttpRequestBase httpMethod) {
        MultivaluedMap<String, String> headers = request.getHeaders().asMap();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            List<String> values = header.getValue();
            for (String value : values) {
                httpMethod.addHeader(header.getKey(), value);
            }
        }
    }

    public boolean isChunked() {
        return chunked;
    }

    public void setChunked(boolean chunked) {
        this.chunked = chunked;
    }

    @Override
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    @Override
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * If passed httpMethod is of type HttpPost then obtain its entity. If the entity has an enclosing File then
     * delete it by invoking this method after the request has completed. The entity will have an enclosing File
     * only if it was too huge to fit into memory.
     *
     * @param httpMethod - the httpMethod to clean up.
     */
    protected void cleanUpAfterExecute(final HttpRequestBase httpMethod) {
    }

    /**
     * Build the HttpEntity to be sent to the Service as part of (POST) request. Creates a off-memory
     * {@link FileEntity} or a regular in-memory {@link ByteArrayEntity} depending on if the request
     * OutputStream fit into memory when built by calling.
     *
     * @param request -
     * @return - the built HttpEntity
     * @throws IOException -
     */
    protected HttpEntity buildEntity(final ClientInvocation request) throws IOException {
        AbstractHttpEntity entityToBuild = null;
        try (ClientEntityOutputStream entityStream = writeRequestBodyToOutputStream(request)) {

            MediaType mediaType = request.getHeaders().getMediaType();
            entityToBuild = entityStream.toEntity();
            if (mediaType != null) {
                entityToBuild
                        .setContentType(new BasicHeader(HTTP.CONTENT_TYPE, mediaType.toString()));
            }
            if (request.isChunked()) {
                entityToBuild.setChunked(true);
            }
        }
        return entityToBuild;
    }

    /**
     * Creates the request OutputStream, to be sent to the end Service invoked, as a
     * <a href="http://commons.apache.org/io/api-release/org/apache/commons/io/output/DeferredFileOutputStream.html"
     * >DeferredFileOutputStream</a>.
     *
     *
     * @param request -
     * @return - DeferredFileOutputStream with the ClientRequest written out per HTTP specification.
     * @throws IOException -
     */
    private ClientEntityOutputStream writeRequestBodyToOutputStream(final ClientInvocation request) throws IOException {
        try (
                ClientEntityOutputStream entityStream = new ClientEntityOutputStream(
                        Threshold.of(this.fileUploadInMemoryThresholdLimit, this.fileUploadMemoryUnit.toSizeUnit()),
                        this.fileUploadTempFileDir, this::getTempfilePrefix)) {
            request.getDelegatingOutputStream().setDelegate(entityStream);
            request.writeRequestBody(request.getEntityStream());
            return entityStream;
        }
    }

    /**
     * Use context information, which will include node name, to avoid conflicts in case of multiple VMS using same
     * temp directory location.
     *
     * @return -
     */
    protected String getTempfilePrefix() {
        return processId;
    }

    protected HttpClient createDefaultHttpClient() {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        if (defaultProxy != null) {
            requestBuilder.setProxy(defaultProxy);
        }
        builder.disableContentCompression();
        builder.setDefaultRequestConfig(requestBuilder.build());
        return builder.build();
    }

    public HttpHost getDefaultProxy() {
        Configurable clientConfiguration = (Configurable) httpClient;
        return clientConfiguration.getConfig().getProxy();
    }

    protected void setRedirectRequired(final ClientInvocation request, final HttpRequestBase httpMethod) {
        RequestConfig.Builder requestBuilder = RequestConfig.copy(getCurrentConfiguration(request, httpMethod));
        requestBuilder.setRedirectsEnabled(true);
        httpMethod.setConfig(requestBuilder.build());
    }

    protected void setRedirectNotRequired(final ClientInvocation request, final HttpRequestBase httpMethod) {
        RequestConfig.Builder requestBuilder = RequestConfig.copy(getCurrentConfiguration(request, httpMethod));
        requestBuilder.setRedirectsEnabled(false);
        httpMethod.setConfig(requestBuilder.build());
    }

    private RequestConfig getCurrentConfiguration(final ClientInvocation request, final HttpRequestBase httpMethod) {
        RequestConfig baseConfig;
        if (httpMethod != null && httpMethod.getConfig() != null) {
            baseConfig = httpMethod.getConfig();
        } else {
            ManualClosingApacheHttpClient43Engine engine = ((ManualClosingApacheHttpClient43Engine) request.getClient()
                    .httpEngine());
            baseConfig = ((Configurable) engine.getHttpClient()).getConfig();
            if (baseConfig == null) {
                Configurable clientConfiguration = (Configurable) httpClient;
                baseConfig = clientConfiguration.getConfig();
            }
        }
        return baseConfig;
    }

    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void close() {
        autoClosed.set(false);
        cleanable.clean();
    }

    private static Cleaner.Cleanable createCleanable(final ManualClosingApacheHttpClient43Engine engine,
            final boolean allowClose,
            final AtomicBoolean closed, final AtomicBoolean autoClosed, final HttpClient client) {
        if (allowClose && client instanceof CloseableHttpClient) {
            return ResourceCleaner.register(engine, new CleanupAction(closed, autoClosed, (CloseableHttpClient) client));
        }
        return () -> closed.set(true);
    }

    private static Path getTempDir() {
        return Path.of(getProperty("java.io.tmpdir", String.class, () -> System.getProperty("java.io.tmpdir")));
    }

    private static <T> T getProperty(final String name, final Class<T> type, final Supplier<T> dft) {
        if (System.getSecurityManager() == null) {
            return ConfigurationFactory.getInstance().getConfiguration().getOptionalValue(name, type).orElseGet(dft);
        }
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> ConfigurationFactory.getInstance()
                .getConfiguration().getOptionalValue(name, type).orElseGet(dft));
    }

}
