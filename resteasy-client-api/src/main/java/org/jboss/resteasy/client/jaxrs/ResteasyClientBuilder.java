package org.jboss.resteasy.client.jaxrs;

import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Configuration;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Abstraction for creating Clients. Allows SSL configuration. Uses Apache Http Client under
 * the covers. If used with other ClientHttpEngines though, all configuration options are ignored.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ResteasyClientBuilder extends ClientBuilder {
    public enum HostnameVerificationPolicy {
        /**
         * Hostname verification is not done on the server's certificate
         */
        ANY,
        /**
         * Allows wildcards in subdomain names i.e. *.foo.com
         */
        WILDCARD,
        /**
         * CN must match hostname connecting to
         */
        STRICT
    }

    /**
     * Client properties to enable proxy. Proxy host property name (string).
     */
    public static final String PROPERTY_PROXY_HOST = "org.jboss.resteasy.jaxrs.client.proxy.host";
    /**
     * Proxy port property name (integer).
     */
    public static final String PROPERTY_PROXY_PORT = "org.jboss.resteasy.jaxrs.client.proxy.port";
    /**
     * Proxy scheme property name (string).
     */
    public static final String PROPERTY_PROXY_SCHEME = "org.jboss.resteasy.jaxrs.client.proxy.scheme";

    public static final String PROPERTY_FOLLOW_REDIRECTS = "dev.resteasy.client.follow.redirects";

    /**
     * Changing the providerFactory will wipe clean any registered components or properties.
     *
     * @param providerFactory provider factory
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder providerFactory(ResteasyProviderFactory providerFactory);

    public abstract ResteasyProviderFactory getProviderFactory();

    /**
     * If there is a connection pool, set the time to live in the pool.
     *
     * @param ttl  time to live
     * @param unit the time unit of the ttl argument
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder connectionTTL(long ttl, TimeUnit unit);

    public abstract long getConnectionTTL(TimeUnit unit);

    /**
     * If connection pooling enabled, how many connections to pool per url?
     *
     * @param maxPooledPerRoute max pool size per url
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder maxPooledPerRoute(int maxPooledPerRoute);

    public abstract int getMaxPooledPerRoute();

    /**
     * If connection pooling is enabled, how long will we wait to get a connection?
     *
     * @param timeout the timeout
     * @param unit    the units the timeout is in
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder connectionCheckoutTimeout(long timeout, TimeUnit unit);

    public abstract long getConnectionCheckoutTimeout(TimeUnit unit);

    /**
     * Number of connections allowed to pool.
     *
     * @param connectionPoolSize connection pool size
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder connectionPoolSize(int connectionPoolSize);

    public abstract int getConnectionPoolSize();

    /**
     * Response stream is wrapped in a BufferedInputStream. Default is 8192. Value of 0 will not wrap it.
     * Value of -1 will use a SelfExpandingBufferedInputStream.
     *
     * @param size response buffer size
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder responseBufferSize(int size);

    public abstract int getResponseBufferSize();

    /**
     * Disable trust management and hostname verification. <i>NOTE</i> this is a security
     * hole, so only set this option if you cannot or do not want to verify the identity of the
     * host you are communicating with.
     *
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder disableTrustManager();

    public abstract boolean isTrustManagerDisabled();

    /**
     * When the user is not using a TrustManager (see disableTrustManager) and
     * does not define an SSLContext object but they want all defined trustStores
     * to use the TrustSelfSignedCertificates trust strategy set this value to true.
     *
     * @param b A value of true assigns trust strategy TrustSelfSignedCertificates
     *          to the trustStores. A value of false assigns a null to the trust
     *          strategy. The default value is true in order to maintain backward
     *          compatibility.
     */
    public abstract void setIsTrustSelfSignedCertificates(boolean b);

    public abstract boolean isTrustSelfSignedCertificates();

    /**
     * SSL policy used to verify hostnames
     *
     * @param policy SSL policy
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder hostnameVerification(HostnameVerificationPolicy policy);

    public abstract HostnameVerificationPolicy getHostnameVerification();

    /**
     * Negates all ssl and connection specific configuration
     *
     * @param httpEngine http engine
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder httpEngine(ClientHttpEngine httpEngine);

    public abstract ClientHttpEngine getHttpEngine();

    public abstract ResteasyClientBuilder useAsyncHttpEngine();

    public abstract boolean isUseAsyncHttpEngine();

    /**
     * Adds a TLS/SSL SNI Host Name for authentication.
     *
     * @param sniHostNames host names
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder sniHostNames(String... sniHostNames);

    public abstract List<String> getSniHostNames();

    /**
     * Specify a default proxy. Default port and schema will be used.
     *
     * @param hostname host name
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder defaultProxy(String hostname);

    public abstract String getDefaultProxyHostname();

    public abstract int getDefaultProxyPort();

    public abstract String getDefaultProxyScheme();

    public abstract boolean isUseJvmProxySettings();

    /**
     * Specify a default proxy host and port. Default schema will be used.
     *
     * @param hostname host name
     * @param port     port
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder defaultProxy(String hostname, int port);

    /**
     * Specify default proxy.
     *
     * @param hostname host name
     * @param port     port
     * @param scheme   scheme
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder defaultProxy(String hostname, int port, String scheme);

    /**
     * Specify if the JVM proxy settings should be used.
     *
     * @param useJvmProxySettings a boolean indicating whether the JVM's global proxy
     *                            settings should be used for the client connections.
     * @return an updated client builder instance
     */
    public abstract ResteasyClientBuilder useJvmProxySettings(boolean useJvmProxySettings);

    /**
     * Enable state (cookie) management.
     *
     * @return the updated client builder instance
     */
    public abstract ResteasyClientBuilder enableCookieManagement();

    public abstract boolean isCookieManagementEnabled();

    public abstract SSLContext getSSLContext();

    public abstract KeyStore getKeyStore();

    public abstract String getKeyStorePassword();

    public abstract KeyStore getTrustStore();

    public abstract HostnameVerifier getHostnameVerifier();

    public abstract long getReadTimeout(TimeUnit unit);

    public abstract long getConnectionTimeout(TimeUnit unit);

    /**
     * boolean, notify apache to disable its automatic retries.
     */
    public abstract ResteasyClientBuilder disableAutomaticRetries();

    public abstract boolean isDisableAutomaticRetries();

    public abstract ResteasyClientBuilder executorService(ExecutorService executorService, boolean cleanupExecutor);

    @Override
    public abstract ResteasyClient build();

    @Override
    public abstract ResteasyClientBuilder withConfig(Configuration config);

    @Override
    public abstract ResteasyClientBuilder sslContext(SSLContext sslContext);

    @Override
    public abstract ResteasyClientBuilder keyStore(KeyStore keyStore, char[] password);

    @Override
    public abstract ResteasyClientBuilder keyStore(KeyStore keyStore, String password);

    @Override
    public abstract ResteasyClientBuilder trustStore(KeyStore trustStore);

    @Override
    public abstract ResteasyClientBuilder hostnameVerifier(HostnameVerifier verifier);

    @Override
    public abstract ResteasyClientBuilder executorService(ExecutorService executorService);

    @Override
    public abstract ResteasyClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService);

    @Override
    public abstract ResteasyClientBuilder connectTimeout(long timeout, TimeUnit unit);

    @Override
    public abstract ResteasyClientBuilder readTimeout(long timeout, TimeUnit unit);

    /**
     * Follow redirects added for MicroProfile-rest-client but can be used by
     * tradition clients as well.
     *
     * @param followRedirects
     * @return
     */
    public abstract ResteasyClientBuilder setFollowRedirects(boolean followRedirects);

    public abstract boolean isFollowRedirects();
}
