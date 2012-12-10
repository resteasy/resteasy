package org.jboss.resteasy.client.jaxrs;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractClientBuilder
{
   public static enum HostnameVerificationPolicy
   {
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
    * SSLContext used to create socket connections.  This will negate any other SSL settings.
    *
    * @param sslContext
    * @return
    */
   public abstract ResteasyClientBuilder sslContext(SSLContext sslContext);


   public abstract ResteasyClientBuilder connectionTTL(long ttl, TimeUnit unit);

   /**
    * How many connections are allowed to be pooled per hostname
    *
    * @param maxPooledPerRoute
    * @return
    */
   public abstract ResteasyClientBuilder maxPooledPerRoute(int maxPooledPerRoute);

   /**
    * Client will cache N number of http connections.
    *
    * @param connectionPoolSize
    * @return
    */
   public abstract ResteasyClientBuilder connectionPoolSize(int connectionPoolSize);

   /**
    * Truststore used to verify server https connections
    *
    * @param truststore
    * @return
    */
   public abstract ResteasyClientBuilder truststore(KeyStore truststore);

   /**
    * Client keystore to use when doing 2-way TLS (client cert auth).
    *
    * @param clientKeyStore
    * @param password
    * @return
    */
   public abstract ResteasyClientBuilder clientKeyStore(KeyStore clientKeyStore, String password);

   /**
    * Turns off server certificate verification.  This will allow MITM attacks so use this feature with caution!
    *
    */
   public abstract ResteasyClientBuilder disableTrustManager();

   /**
    * SSL policy used to verify hostnames
    *
    * @param policy
    * @return
    */
   public abstract ResteasyClientBuilder hostnameVerification(HostnameVerificationPolicy policy);

   public abstract ResteasyClientBuilder property(String name, Object value);

   public abstract ResteasyClient build();

}
