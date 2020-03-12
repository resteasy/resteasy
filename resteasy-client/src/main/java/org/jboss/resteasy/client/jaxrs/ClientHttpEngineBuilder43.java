package org.jboss.resteasy.client.jaxrs;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
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
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.PassthroughTrustManager;
import org.jboss.resteasy.client.jaxrs.engines.factory.ApacheHttpClient4EngineFactory;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

public class ClientHttpEngineBuilder43 implements ClientHttpEngineBuilder {

   private ResteasyClientBuilder that;

   @Override
   public ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder)
   {
      that = resteasyClientBuilder;
      return this;
   }

   @Override
   public ClientHttpEngine build()
   {
      HostnameVerifier verifier = null;
      if (that.verifier != null) {
         verifier = new VerifierWrapper(that.verifier);
      }
      else
      {
         switch (that.policy)
         {
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
      try
      {
         SSLConnectionSocketFactory sslsf = null;
         SSLContext theContext = that.sslContext;
         Iterator clientConfigProviderIterator = ServiceLoader.load(ClientConfigProvider.class).iterator();
         if (that.disableTrustManager)
         {
            theContext = SSLContext.getInstance("SSL");
            theContext.init(null, new TrustManager[]{new PassthroughTrustManager()},
               new SecureRandom());
            verifier = new NoopHostnameVerifier();
            sslsf = new SSLConnectionSocketFactory(theContext, verifier);
         }
         else if (theContext != null)
         {
            sslsf = new SSLConnectionSocketFactory(theContext, verifier) {
               @Override
               protected void prepareSocket(SSLSocket socket) throws IOException
               {
                  if(!that.sniHostNames.isEmpty()) {
                     List<SNIServerName> sniNames = new ArrayList<>(that.sniHostNames.size());
                     for(String sniHostName : that.sniHostNames) {
                        sniNames.add(new SNIHostName(sniHostName));
                     }

                     SSLParameters sslParameters = socket.getSSLParameters();
                     sslParameters.setServerNames(sniNames);
                     socket.setSSLParameters(sslParameters);
                  }
               }
            };
         }
         else if (that.clientKeyStore != null || that.truststore != null)
         {
            SSLContext ctx = SSLContexts.custom()
               .useProtocol(SSLConnectionSocketFactory.TLS)
               .setSecureRandom(null)
               .loadKeyMaterial(that.clientKeyStore,
                        that.clientPrivateKeyPassword != null ? that.clientPrivateKeyPassword.toCharArray() : null)
               .loadTrustMaterial(that.truststore,
                       that.isTrustSelfSignedCertificates() ? TrustSelfSignedStrategy.INSTANCE : null)
               .build();
            sslsf = new SSLConnectionSocketFactory(ctx, verifier) {
               @Override
               protected void prepareSocket(SSLSocket socket) throws IOException
               {
                  that.prepareSocketForSni(socket);
               }
            };
         } else if (clientConfigProviderIterator.hasNext())
         {
            // delegate creation of socket to ClientConfigProvider implementation
            final ClientConfigProvider configProvider = ((ClientConfigProvider) clientConfigProviderIterator.next());
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
         }
         else
         {
            final SSLContext tlsContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            tlsContext.init(null, null, null);
            sslsf = new SSLConnectionSocketFactory(tlsContext, verifier);
         }

         final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslsf)
            .build();

         HttpClientConnectionManager cm = null;
         if (that.connectionPoolSize > 0)
         {
            PoolingHttpClientConnectionManager tcm = new PoolingHttpClientConnectionManager(
               registry, null, null ,null, that.connectionTTL, that.connectionTTLUnit);
            tcm.setMaxTotal(that.connectionPoolSize);
            if (that.maxPooledPerRoute == 0) {
               that.maxPooledPerRoute = that.connectionPoolSize;
            }
            tcm.setDefaultMaxPerRoute(that.maxPooledPerRoute);
            cm = tcm;

         }
         else
         {
            cm = new BasicHttpClientConnectionManager(registry);
         }

         RequestConfig.Builder rcBuilder = RequestConfig.custom();
         if (that.socketTimeout > -1)
         {
            rcBuilder.setSocketTimeout((int) that.socketTimeoutUnits.toMillis(that.socketTimeout));
         }
         if (that.establishConnectionTimeout > -1)
         {
            rcBuilder.setConnectTimeout((int)that.establishConnectionTimeoutUnits.toMillis(that.establishConnectionTimeout));
         }
         if (that.connectionCheckoutTimeoutMs > -1)
         {
            rcBuilder.setConnectionRequestTimeout(that.connectionCheckoutTimeoutMs);
         }

         return createEngine(cm, rcBuilder, that.defaultProxy, that.responseBufferSize, verifier, theContext);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected ClientHttpEngine createEngine(final HttpClientConnectionManager cm, final RequestConfig.Builder rcBuilder,
         final HttpHost defaultProxy, final int responseBufferSize, final HostnameVerifier verifier, final SSLContext theContext)
   {
      final HttpClient httpClient;
      rcBuilder.setProxy(that.defaultProxy);
      if (System.getSecurityManager() == null)
      {
         httpClient = HttpClientBuilder.create()
                 .setConnectionManager(cm)
                 .setDefaultRequestConfig(rcBuilder.build())
                 .disableContentCompression().build();
      }
      else
      {
         httpClient = AccessController.doPrivileged(new PrivilegedAction<HttpClient>()
         {
            @Override
            public HttpClient run()
            {
               return HttpClientBuilder.create()
                        .setConnectionManager(cm)
                        .setDefaultRequestConfig(rcBuilder.build())
                        .disableContentCompression().build();
            }
         });
      }

      ApacheHttpClient43Engine engine = (ApacheHttpClient43Engine) ApacheHttpClient4EngineFactory.create(httpClient,
            true);
      engine.setResponseBufferSize(responseBufferSize);
      engine.setHostnameVerifier(verifier);
      // this may be null.  We can't really support this with Apache Client.
      engine.setSslContext(theContext);
      return engine;
   }
}
