package org.jboss.resteasy.client.jaxrs.engines;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContexts;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngineBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.spi.PriorityServiceLoader;

/**
 *
 * @deprecated This will be removed in a future release as the underlying default implementation of the
 *             {@link org.jboss.resteasy.client.jaxrs.ClientHttpEngineBuilder} will be replaced.
 */
@Deprecated(forRemoval = true, since = "6.2")
public class ClientHttpEngineBuilder43 implements ClientHttpEngineBuilder {

    private ResteasyClientBuilder that;

    @Override
    public ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder) {
        that = resteasyClientBuilder;
        return this;
    }

    @Override
    public ClientHttpEngine build() {
        HostnameVerifier verifier = null;
        if (that.getHostnameVerifier() != null) {
            verifier = that.getHostnameVerifier();
        } else {
            switch (that.getHostnameVerification()) {
                case ANY:
                    verifier = new NoopHostnameVerifier();
                    break;
                case WILDCARD:
                    verifier = new DefaultHostnameVerifier();
                    break;
                case STRICT:
                    //this will load default file from httplcient.jar!/mozilla/public-suffix-list.txt
                    //if this default file isn't what you want, set a customized HostNameVerifier
                    //to ResteasyClientBuilder instead
                    verifier = new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
                    break;
            }
        }
        try {
            SSLConnectionSocketFactory sslsf = null;
            SSLContext theContext = that.getSSLContext();
            final ClientConfigProvider configProvider = findClientConfigProvider();

            if (that.isTrustManagerDisabled()) {
                theContext = SSLContext.getInstance("SSL");
                theContext.init(null, new TrustManager[] { new PassthroughTrustManager() },
                        new SecureRandom());
                verifier = new NoopHostnameVerifier();
                sslsf = new SSLConnectionSocketFactory(theContext, verifier);
            } else if (theContext != null) {
                sslsf = new SSLConnectionSocketFactory(theContext, verifier) {
                    @Override
                    protected void prepareSocket(SSLSocket socket) throws IOException {
                        if (!that.getSniHostNames().isEmpty()) {
                            List<SNIServerName> sniNames = new ArrayList<>(that.getSniHostNames().size());
                            for (String sniHostName : that.getSniHostNames()) {
                                sniNames.add(new SNIHostName(sniHostName));
                            }

                            SSLParameters sslParameters = socket.getSSLParameters();
                            sslParameters.setServerNames(sniNames);
                            socket.setSSLParameters(sslParameters);
                        }
                    }
                };
            } else if (that.getKeyStore() != null || that.getTrustStore() != null) {
                SSLContext ctx = SSLContexts.custom()
                        .setProtocol(SSLConnectionSocketFactory.TLS)
                        .setSecureRandom(null)
                        .loadKeyMaterial(that.getKeyStore(),
                                that.getKeyStorePassword() != null ? that.getKeyStorePassword().toCharArray() : null)
                        .loadTrustMaterial(that.getTrustStore(),
                                that.isTrustSelfSignedCertificates() ? TrustSelfSignedStrategy.INSTANCE : null)
                        .build();

                sslsf = new SSLConnectionSocketFactory(ctx, verifier) {
                    @Override
                    protected void prepareSocket(SSLSocket socket) throws IOException {
                        List<String> sniHostNames = that.getSniHostNames();
                        if (!sniHostNames.isEmpty()) {
                            List<SNIServerName> sniNames = new ArrayList<>(sniHostNames.size());
                            for (String sniHostName : sniHostNames) {
                                sniNames.add(new SNIHostName(sniHostName));
                            }

                            SSLParameters sslParameters = socket.getSSLParameters();
                            sslParameters.setServerNames(sniNames);
                            socket.setSSLParameters(sslParameters);
                        }
                    }
                };
            } else if (configProvider != null) {
                // delegate creation of socket to ClientConfigProvider implementation
                sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), verifier) {
                    @Override
                    public Socket createSocket(HttpContext context) throws IOException {
                        try {
                            String targetHostUri = context.getAttribute(
                                    HttpCoreContext.HTTP_TARGET_HOST).toString();
                            if (targetHostUri != null) {
                                return configProvider.getSSLContext(new URI(targetHostUri)).getSocketFactory().createSocket();
                            } else {
                                throw new RuntimeException("URI is not known");
                            }
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            } else {
                final SSLContext tlsContext = SSLContext.getDefault();
                sslsf = new SSLConnectionSocketFactory(tlsContext, verifier);
            }

            final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();

            final HttpClientConnectionManager cm;
            final long readTimeout = that.getReadTimeout(TimeUnit.MILLISECONDS);
            final SocketConfig socketConfig;
            if (readTimeout > 0) {
                socketConfig = SocketConfig.custom()
                        .setSoTimeout(Math.toIntExact(readTimeout))
                        .build();
            } else {
                socketConfig = SocketConfig.DEFAULT;
            }
            if (that.getConnectionPoolSize() > 0) {
                PoolingHttpClientConnectionManager tcm = new PoolingHttpClientConnectionManager(
                        registry, null, null, null, that.getConnectionTTL(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
                tcm.setMaxTotal(that.getConnectionPoolSize());
                if (that.getMaxPooledPerRoute() == 0) {
                    that.maxPooledPerRoute(that.getConnectionPoolSize());
                }
                tcm.setDefaultMaxPerRoute(that.getMaxPooledPerRoute());
                tcm.setDefaultSocketConfig(socketConfig);
                cm = tcm;
            } else {
                BasicHttpClientConnectionManager bcm = new BasicHttpClientConnectionManager(registry);
                bcm.setSocketConfig(socketConfig);
                cm = bcm;
            }

            RequestConfig.Builder rcBuilder = RequestConfig.custom();
            if (readTimeout > -1) {
                rcBuilder.setSocketTimeout((int) readTimeout);
            }
            if (that.getConnectionTimeout(TimeUnit.MILLISECONDS) > -1) {
                rcBuilder.setConnectTimeout((int) that.getConnectionTimeout(TimeUnit.MILLISECONDS));
            }
            if (that.getConnectionCheckoutTimeout(TimeUnit.MILLISECONDS) > -1) {
                rcBuilder.setConnectionRequestTimeout((int) that.getConnectionCheckoutTimeout(TimeUnit.MILLISECONDS));
            }

            return createEngine(cm, rcBuilder, getDefaultProxy(that), that.getResponseBufferSize(), verifier, theContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpHost getDefaultProxy(ResteasyClientBuilder that) {
        String hostName = that.getDefaultProxyHostname();
        return hostName != null ? new HttpHost(hostName, that.getDefaultProxyPort(), that.getDefaultProxyScheme()) : null;
    }

    protected ClientHttpEngine createEngine(final HttpClientConnectionManager cm, final RequestConfig.Builder rcBuilder,
            final HttpHost defaultProxy, final int responseBufferSize, final HostnameVerifier verifier,
            final SSLContext theContext) {
        final HttpClient httpClient;
        rcBuilder.setProxy(defaultProxy);
        // This is somewhat an arbitrary number of seconds to run the eviction thread at. However, this is the default
        // in WildFly so we will use it. We are not exposing this as a property as other clients may not require a
        // a setting like this.
        final long maxIdleTime = 60L;
        if (System.getSecurityManager() == null) {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .evictIdleConnections(maxIdleTime, TimeUnit.SECONDS)
                    .setDefaultRequestConfig(rcBuilder.build())
                    .disableContentCompression();
            if (!that.isCookieManagementEnabled()) {
                httpClientBuilder.disableCookieManagement();
            }
            if (that.isDisableAutomaticRetries()) {
                httpClientBuilder.disableAutomaticRetries();
            }
            httpClient = httpClientBuilder.build();
        } else {
            httpClient = AccessController.doPrivileged(new PrivilegedAction<HttpClient>() {
                @Override
                public HttpClient run() {
                    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                            .setConnectionManager(cm)
                            .evictExpiredConnections()
                            .evictIdleConnections(maxIdleTime, TimeUnit.SECONDS)
                            .setDefaultRequestConfig(rcBuilder.build())
                            .disableContentCompression();
                    if (!that.isCookieManagementEnabled()) {
                        httpClientBuilder.disableCookieManagement();
                    }
                    if (that.isDisableAutomaticRetries()) {
                        httpClientBuilder.disableAutomaticRetries();
                    }
                    return httpClientBuilder.build();
                }
            });
        }

        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient, true);
        engine.setResponseBufferSize(responseBufferSize);
        engine.setHostnameVerifier(verifier);
        // this may be null.  We can't really support this with Apache Client.
        engine.setSslContext(theContext);
        engine.setFollowRedirects(that.isFollowRedirects());
        return engine;
    }

    private static ClientConfigProvider findClientConfigProvider() {
        if (System.getSecurityManager() == null) {
            return PriorityServiceLoader.load(ClientConfigProvider.class, getClassLoader(ClientConfigProvider.class)).first()
                    .orElse(null);
        }
        return AccessController.doPrivileged((PrivilegedAction<ClientConfigProvider>) () -> PriorityServiceLoader
                .load(ClientConfigProvider.class, getClassLoader(ClientConfigProvider.class)).first().orElse(null));
    }

    private static ClassLoader getClassLoader(final Class<?> type) {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = type.getClassLoader();
        }
        return result;
    }
}
