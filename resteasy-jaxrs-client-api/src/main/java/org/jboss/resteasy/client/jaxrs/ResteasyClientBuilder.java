package org.jboss.resteasy.client.jaxrs;

import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Abstraction for creating Clients.  Allows SSL configuration.  Uses Apache Http Client under
 * the covers.  If used with other ClientHttpEngines though, all configuration options are ignored.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ResteasyClientBuilder extends ClientBuilder
{
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
    * @param ttl time to live
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
    * @param timeout the timeout
    * @param unit the units the timeout is in
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
    * Response stream is wrapped in a BufferedInputStream.  Default is 8192.  Value of 0 will not wrap it.
    * Value of -1 will use a SelfExpandingBufferedInputStream.
    *
    * @param size response buffer size
    * @return an updated client builder instance
    */
   public abstract ResteasyClientBuilder responseBufferSize(int size);

   public abstract int getResponseBufferSize();

   /**
    * Disable trust management and hostname verification.  <i>NOTE</i> this is a security
    * hole, so only set this option if you cannot or do not want to verify the identity of the
    * host you are communicating with.
    * @return an updated client builder instance
    */
   public abstract ResteasyClientBuilder disableTrustManager();

   public abstract boolean isTrustManagerDisabled();

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
    * Specify a default proxy.  Default port and schema will be used.
    *
    * @param hostname host name
    * @return an updated client builder instance
    */
   public abstract ResteasyClientBuilder defaultProxy(String hostname);

   public abstract String getDefaultProxyHostname();

   public abstract int getDefaultProxyPort();

   public abstract String getDefaultProxyScheme();

   /**
    * Specify a default proxy host and port.  Default schema will be used.
    *
    * @param hostname host name
    * @param port port
    * @return an updated client builder instance
    */
   public abstract ResteasyClientBuilder defaultProxy(String hostname, int port);

   /**
    * Specify default proxy.
    *
    * @param hostname host name
    * @param port port
    * @param scheme scheme
    * @return an updated client builder instance
    */
   public abstract ResteasyClientBuilder defaultProxy(String hostname, int port, String scheme);

   public abstract SSLContext getSSLContext();

   public abstract KeyStore getKeyStore();

   public abstract String getKeyStorePassword();

   public abstract KeyStore getTrustStore();

   public abstract HostnameVerifier getHostnameVerifier();

   public abstract long getReadTimeout(TimeUnit unit);

   public abstract long getConnectionTimeout(TimeUnit unit);

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
}
