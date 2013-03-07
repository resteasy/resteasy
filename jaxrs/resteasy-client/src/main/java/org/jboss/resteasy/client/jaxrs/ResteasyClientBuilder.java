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
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
public class ResteasyClientBuilder extends ClientBuilder
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
   protected HostnameVerifier verifier = null;

   /**
    * Changing the providerFactory will wipe clean any registered components or properties.
    *
    * @param providerFactory
    * @return
    */
   public ResteasyClientBuilder providerFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
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

   public ResteasyClientBuilder connectionTTL(long ttl, TimeUnit unit)
   {
      this.connectionTTL = ttl;
      this.connectionTTLUnit = unit;
      return this;
   }

   public ResteasyClientBuilder maxPooledPerRoute(int maxPooledPerRoute)
   {
      this.maxPooledPerRoute = maxPooledPerRoute;
      return this;
   }

   public ResteasyClientBuilder connectionPoolSize(int connectionPoolSize)
   {
      this.connectionPoolSize = connectionPoolSize;
      return this;
   }

   /**
    * Disable trust management and hostname verification.  <i>NOTE</i> this is a security
    * hole, so only set this option if you cannot or do not want to verify the identity of the
    * host you are communicating with.
    */
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
   public ResteasyClientBuilder hostnameVerification(HostnameVerificationPolicy policy)
   {
      this.policy = policy;
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
   public ResteasyClientBuilder sslContext(SSLContext sslContext)
   {
      this.sslContext = sslContext;
      return this;
   }

   @Override
   public ResteasyClientBuilder trustStore(KeyStore truststore)
   {
      this.truststore = truststore;
      return this;
   }

   @Override
   public ResteasyClientBuilder keyStore(KeyStore keyStore, String password)
   {
      this.clientKeyStore = keyStore;
      this.clientPrivateKeyPassword = password;
      return this;
   }

   @Override
   public ResteasyClientBuilder keyStore(KeyStore keyStore, char[] password)
   {
      this.clientKeyStore = keyStore;
      this.clientPrivateKeyPassword = new String(password);
      return this;
   }

   @Override
   public ResteasyClientBuilder property(String name, Object value)
   {
      getProviderFactory().property(name, value);
      return this;
   }

   protected ResteasyProviderFactory getProviderFactory()
   {
      if (providerFactory == null)
      {
         // create a new one
         providerFactory = new ResteasyProviderFactory();
         RegisterBuiltin.register(providerFactory);
      }
      return providerFactory;
   }

   @Override
   public ResteasyClient build()
   {
      ClientConfiguration config = new ClientConfiguration(getProviderFactory());
      for (Map.Entry<String, Object> entry : properties.entrySet())
      {
         config.property(entry.getKey(), entry.getValue());
      }
      if (asyncExecutor == null)
      {
         asyncExecutor = Executors.newFixedThreadPool(10);
      }

      if (httpEngine == null) httpEngine = initDefaultEngine();
      return new ResteasyClient(httpEngine, asyncExecutor, config);


   }

   static class VerifierWrapper implements X509HostnameVerifier
   {
      protected HostnameVerifier verifier;

      VerifierWrapper(HostnameVerifier verifier)
      {
         this.verifier = verifier;
      }

      @Override
      public void verify(String host, SSLSocket ssl) throws IOException
      {
         if (!verifier.verify(host, ssl.getSession())) throw new SSLException("Hostname verification failure");
      }

      @Override
      public void verify(String host, X509Certificate cert) throws SSLException
      {
         throw new SSLException("This verification path not implemented");
      }

      @Override
      public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
      {
         throw new SSLException("This verification path not implemented");
      }

      @Override
      public boolean verify(String s, SSLSession sslSession)
      {
         return verifier.verify(s, sslSession);
      }
   }

   protected ClientHttpEngine initDefaultEngine()
   {
      DefaultHttpClient httpClient = null;

      X509HostnameVerifier verifier = null;
      if (this.verifier != null) verifier = new VerifierWrapper(this.verifier);
      else
      {
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
      }
      try
      {
         SSLSocketFactory sslsf = null;
         SSLContext theContext = sslContext;
         if (disableTrustManager)
         {
            theContext = SSLContext.getInstance("SSL");
            theContext.init(null, new TrustManager[]{new PassthroughTrustManager()},
                    new SecureRandom());
            verifier =  new AllowAllHostnameVerifier();
            sslsf = new SSLSocketFactory(theContext, verifier);
         }
         else if (theContext != null)
         {
            sslsf = new SSLSocketFactory(theContext, verifier);
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
         ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient, true);
         engine.setHostnameVerifier(verifier);
         // this may be null.  We can't really support this with Apache Client.
         engine.setSslContext(theContext);
         return engine;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public ResteasyClientBuilder hostnameVerifier(HostnameVerifier verifier)
   {
      this.verifier = verifier;
      return this;
   }

   @Override
   public Configuration getConfiguration()
   {
      return getProviderFactory().getConfiguration();
   }

   @Override
   public ResteasyClientBuilder register(Class<?> componentClass)
   {
      getProviderFactory().register(componentClass);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Class<?> componentClass, int priority)
   {
      getProviderFactory().register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Class<?> componentClass, Class<?>... contracts)
   {
      getProviderFactory().register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      getProviderFactory().register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Object component)
   {
      getProviderFactory().register(component);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Object component, int priority)
   {
      getProviderFactory().register(component, priority);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Object component, Class<?>... contracts)
   {
      getProviderFactory().register(component, contracts);
      return this;
   }

   @Override
   public ResteasyClientBuilder register(Object component, Map<Class<?>, Integer> contracts)
   {
      getProviderFactory().register(component, contracts);
      return this;
   }

   @Override
   public ResteasyClientBuilder replaceWith(Configuration config)
   {
      getProviderFactory().replaceWith(config);
      return this;
   }
}
