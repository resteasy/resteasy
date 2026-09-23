package org.jboss.resteasy.client.jaxrs.engines;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.FinalizedClientResponse;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * @author <a href="mailto:alexey.ogarkov@gmail.com">Alexey Ogarkov</a>
 * @version $Revision: 1 $
 */
public class URLConnectionEngine implements ClientHttpEngine {

    protected SSLContext sslContext;
    protected HostnameVerifier hostnameVerifier;
    protected Integer readTimeout;
    protected Integer connectTimeout;
    protected boolean useJvmProxySettings;
    protected String proxyHost;
    protected Integer proxyPort;
    protected String proxyScheme;
    protected boolean followRedirects;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response invoke(Invocation inv) {
        ClientInvocation request = (ClientInvocation) inv;
        final HttpURLConnection connection;

        final int status;
        try {

            connection = createConnection(request);

            executeRequest(request, connection);

            status = connection.getResponseCode();
        } catch (IOException e) {
            throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(e.toString()), e);
        }

        //Creating response with stream content
        ClientResponse response = new FinalizedClientResponse(request.getClientConfiguration(), RESTEasyTracingLogger.empty()) {
            private InputStream stream;

            @Override
            protected InputStream getInputStream() {
                if (stream == null) {
                    try {
                        stream = (status < 300) ? connection.getInputStream() : connection.getErrorStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                return stream;
            }

            @Override
            protected void setInputStream(InputStream is) {
                stream = is;
                resetEntity();
            }

            @Override
            public void releaseConnection() throws IOException {
                releaseConnection(false);
            }

            @Override
            public void releaseConnection(boolean consumeInputStream) throws IOException {
                InputStream is = getInputStream();
                if (is != null) {
                    // https://docs.oracle.com/javase/8/docs/technotes/guides/net/http-keepalive.html
                    if (consumeInputStream) {
                        while (is.read() > 0) {
                        }
                    }
                    is.close();
                }
                connection.disconnect();
            }

        };

        //Setting attributes
        response.setStatus(status);
        response.setHeaders(getHeaders(connection));

        return response;
    }

    /**
     * Create map with response headers.
     *
     * @param connection - HttpURLConnection
     * @return map key - list of values
     */
    protected MultivaluedMap<String, String> getHeaders(
            final HttpURLConnection connection) {
        MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();

        for (Map.Entry<String, List<String>> header : connection.getHeaderFields()
                .entrySet()) {
            if (header.getKey() != null) {
                for (String value : header.getValue()) {
                    headers.add(header.getKey(), value);
                }
            }
        }
        return headers;
    }

    @Override
    public void close() {
        //empty
    }

    /**
     * Create HttpUrlConnection from ClientInvorcation and set request method.
     *
     * @param request ClientInvocation
     * @return HttpURLConnection with method {@literal &} url already set
     * @throws IOException if url or io exceptions
     */
    protected HttpURLConnection createConnection(final ClientInvocation request) throws IOException {
        Proxy proxy = null;
        if (this.proxyHost != null && this.proxyPort != null) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
        } else if (!this.useJvmProxySettings) {
            proxy = Proxy.NO_PROXY;
        }

        HttpURLConnection connection;
        if (proxy != null) {
            connection = (HttpURLConnection) request.getUri().toURL().openConnection(proxy);
        } else {
            connection = (HttpURLConnection) request.getUri().toURL().openConnection();
        }

        connection.setRequestMethod(request.getMethod());

        if (this.connectTimeout != null) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout != null) {
            connection.setReadTimeout(this.readTimeout);
        }

        if (connection instanceof HttpsURLConnection) {
            if (this.hostnameVerifier != null) {
                ((HttpsURLConnection) connection).setHostnameVerifier(this.hostnameVerifier);
            }
            if (this.sslContext != null) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(this.sslContext.getSocketFactory());
            }
        }

        return connection;
    }

    /**
     * Execute request using HttpURLConnection with body from invocation if needed.
     *
     * @param request    ClientInvocation
     * @param connection HttpURLConnection
     */
    protected void executeRequest(final ClientInvocation request, HttpURLConnection connection) {
        connection.setInstanceFollowRedirects(request.getMethod().equals("GET"));

        if (request.getEntity() != null) {
            if (request.getMethod().equals("GET"))
                throw new ProcessingException(Messages.MESSAGES.getRequestCannotHaveBody());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.getDelegatingOutputStream().setDelegate(baos);
            try {

                request.writeRequestBody(request.getEntityStream());
                baos.close();
                commitHeaders(request, connection);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(baos.toByteArray());
                os.flush();
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else // no body
        {
            commitHeaders(request, connection);
        }
    }

    /**
     * Add headers to HttpURLConnection from ClientInvocation. Should be executed before writing body.
     *
     * @param request    ClientInvocation
     * @param connection HttpURLConnection
     */
    protected void commitHeaders(ClientInvocation request, HttpURLConnection connection) {
        MultivaluedMap<String, String> headers = request.getHeaders().asMap();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            List<String> values = header.getValue();
            for (String value : values) {
                connection.addRequestProperty(header.getKey(), value);
            }
        }
    }

    /**
     * {inheritDoc}
     */
    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * {inheritDoc}
     */
    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setUseJvmProxySettings(boolean useJvmProxySettings) {
        this.useJvmProxySettings = useJvmProxySettings;
    }

    public boolean isUseJvmProxySettings() {
        return this.useJvmProxySettings;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyScheme(String proxyScheme) {
        this.proxyScheme = proxyScheme;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowRedirects() {
        return this.followRedirects;
    }
}
