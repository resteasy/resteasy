package org.jboss.resteasy.client.jaxrs;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.PassthroughTrustManager;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Abstraction for creating Clients.  Allows SSL configuration.  Currently defaults to using Apache Http Client under
 * the covers.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyClientBuilder extends AbstractClientBuilder
{

   protected KeyStore truststore;
   protected KeyStore clientKeyStore;
   protected String clientPrivateKeyPassword;
   protected boolean disableTrustManager;
   protected HostnameVerificationPolicy policy = HostnameVerificationPolicy.WILDCARD;
   protected ResteasyProviderFactory providerFactory;
   protected ExecutorService asyncExecutor;
   protected SSLContext sslContext;
   protected Map<String, Object> properties = new HashMap<String, Object>();
   protected ClientHttpEngine httpEngine;
   protected int connectionPoolSize;
   protected int maxPooledPerRoute = 0;
   protected long connectionTTL = -1;
   protected TimeUnit connectionTTLUnit = TimeUnit.MILLISECONDS;

   /**
    * SSLContext used to create socket connections.  This will negate any other SSL settings.
    *
    * @param sslContext
    * @return
    */
   @Override
   public ResteasyClientBuilder sslContext(SSLContext sslContext)
   {
      this.sslContext = sslContext;
      return this;
   }

   @Override
   public ResteasyClientBuilder connectionTTL(long ttl, TimeUnit unit)
   {
      this.connectionTTL = ttl;
      this.connectionTTLUnit = unit;
      return this;
   }

   @Override
   public ResteasyClientBuilder maxPooledPerRoute(int maxPooledPerRoute)
   {
      this.maxPooledPerRoute = maxPooledPerRoute;
      return this;
   }

   @Override
   public ResteasyClientBuilder connectionPoolSize(int connectionPoolSize)
   {
      this.connectionPoolSize = connectionPoolSize;
      return this;
   }

   @Override
   public ResteasyClientBuilder truststore(KeyStore truststore)
   {
      this.truststore = truststore;
      return this;
   }

   /**
    * Client keystore to use when doing 2-way TLS (client cert auth).
    *
    * @param clientKeyStore
    * @param password private key password
    * @return
    */
   @Override
   public ResteasyClientBuilder clientKeyStore(KeyStore clientKeyStore, String password)
   {
      this.clientKeyStore = clientKeyStore;
      this.clientPrivateKeyPassword = password;
      return this;
   }

   /**
    * Turns off server certificate verification.  This will allow MITM attacks so use this feature with caution!
    *
    */
   @Override
   public ResteasyClientBuilder disableTrustManager()
   {
      this.disableTrustManager = true;
      return this;
   }

   /**
    * SSL policy used to verify hostnames
    *
    * @param policy
    * @return
    */
   @Override
   public ResteasyClientBuilder hostnameVerification(HostnameVerificationPolicy policy)
   {
      this.policy = policy;
      return this;
   }

   /**
    * Executor to use to run AsyncInvoker invocations
    *
    * @param asyncExecutor
    * @return
    */
   public ResteasyClientBuilder asyncExecutor(ExecutorService asyncExecutor)
   {
      this.asyncExecutor = asyncExecutor;
      return this;
   }

   public ResteasyClientBuilder providerFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      return this;
   }

   @Override
   public ResteasyClientBuilder property(String name, Object value)
   {
      properties.put(name, value);
      return this;
   }

   /**
    * Negates all ssl and connection specific configuration
    *
    * @param httpEngine
    * @return
    */
   public ResteasyClientBuilder httpEngine(ClientHttpEngine httpEngine)
   {
      this.httpEngine = httpEngine;
      return this;
   }

   @Override
   public ResteasyClient build()
   {
      if (providerFactory == null) providerFactory = ResteasyProviderFactory.getInstance();
      ClientConfiguration config = new ClientConfiguration(providerFactory);
      for (Map.Entry<String, Object> entry : properties.entrySet())
      {
         config.setProperty(entry.getKey(), entry.getValue());
      }
      if (asyncExecutor == null)
      {
         asyncExecutor = Executors.newFixedThreadPool(10);
      }

      if (httpEngine == null) httpEngine = initDefaultEngine();
      return new ResteasyClient(httpEngine, asyncExecutor, config);


   }

   protected ClientHttpEngine initDefaultEngine()
   {
      DefaultHttpClient httpClient = null;

      X509HostnameVerifier verifier = null;
      switch (policy)
      {
         case ANY:
            verifier = new AllowAllHostnameVerifier();
            break;
         case WILDCARD:
            verifier = new BrowserCompatHostnameVerifier();
            break;
         case STRICT:
            verifier = new StrictHostnameVerifier();
            break;
      }
      try
      {
         SSLSocketFactory sslsf = null;
         if (disableTrustManager)
         {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new PassthroughTrustManager()},
                    new SecureRandom());

            sslsf = new SSLSocketFactory(sslContext);
            sslsf.setHostnameVerifier(new AllowAllHostnameVerifier());
         }
         else if (sslContext != null)
         {
            sslsf = new SSLSocketFactory(sslContext);
            sslsf.setHostnameVerifier(verifier);
        }
         else if (clientKeyStore != null || truststore != null)
         {
            sslsf = new SSLSocketFactory(clientKeyStore, clientPrivateKeyPassword, truststore);
            sslsf.setHostnameVerifier(verifier);
         }
         else if (connectionPoolSize <= 0)
         {
            // no special settings, just return the default
            return new ApacheHttpClient4Engine();
         }
         else
         {
            sslsf = SSLSocketFactory.getSocketFactory();
            sslsf.setHostnameVerifier(verifier);
         }
         SchemeRegistry registry = new SchemeRegistry();
         registry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
         Scheme httpsScheme = new Scheme("https", sslsf, 443);
         registry.register(httpsScheme);
         ClientConnectionManager cm = null;
         if (connectionPoolSize > 0)
         {
            ThreadSafeClientConnManager tcm = new ThreadSafeClientConnManager(registry, connectionTTL, connectionTTLUnit);
            tcm.setMaxTotal(connectionPoolSize);
            if (maxPooledPerRoute == 0) maxPooledPerRoute = connectionPoolSize;
            tcm.setDefaultMaxPerRoute(maxPooledPerRoute);
            cm = tcm;

         }
         else
         {
            cm = new SingleClientConnManager(registry);
         }
         httpClient = new DefaultHttpClient(cm, new BasicHttpParams());
         return new ApacheHttpClient4Engine(httpClient, true);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


}
