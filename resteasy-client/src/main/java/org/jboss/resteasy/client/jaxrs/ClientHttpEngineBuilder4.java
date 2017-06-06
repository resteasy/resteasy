package org.jboss.resteasy.client.jaxrs;

import java.io.IOException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.PassthroughTrustManager;
import org.jboss.resteasy.client.jaxrs.engines.factory.ApacheHttpClient4EngineFactory;

@Deprecated
public class ClientHttpEngineBuilder4 implements ClientHttpEngineBuilder {

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
        X509HostnameVerifier verifier = null;
        if (that.verifier != null) {
            verifier = new VerifierWrapper(that.verifier);
        }
        else
        {
            switch (that.policy)
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
            SSLContext theContext = that.sslContext;
            if (that.disableTrustManager)
            {
                theContext = SSLContext.getInstance("SSL");
                theContext.init(null, new TrustManager[]{new PassthroughTrustManager()},
                    new SecureRandom());
                verifier =  new AllowAllHostnameVerifier();
                sslsf = new SSLSocketFactory(theContext, verifier);
            }
            else if (theContext != null)
            {
                sslsf = new SSLSocketFactory(theContext, verifier) {
                    @Override
                    protected void prepareSocket(SSLSocket socket) throws IOException
                    {
                        that.prepareSocketForSni(socket);
                    }
                };
            }
            else if (that.clientKeyStore != null || that.truststore != null)
            {
                sslsf = new SSLSocketFactory(SSLSocketFactory.TLS,
                    that.clientKeyStore, that.clientPrivateKeyPassword,
                    that.truststore, null, verifier) {
                    @Override
                    protected void prepareSocket(SSLSocket socket) throws IOException
                    {
                        that.prepareSocketForSni(socket);
                    }
                };
            }
            else
            {
                final SSLContext tlsContext = SSLContext.getInstance(SSLSocketFactory.TLS);
                tlsContext.init(null, null, null);
                sslsf = new SSLSocketFactory(tlsContext, verifier);
            }
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            Scheme httpsScheme = new Scheme("https", 443, sslsf);
            registry.register(httpsScheme);
            ClientConnectionManager cm = null;
            if (that.connectionPoolSize > 0)
            {
                PoolingClientConnectionManager tcm =
                    new PoolingClientConnectionManager(registry,
                        that.connectionTTL, that.connectionTTLUnit);
                tcm.setMaxTotal(that.connectionPoolSize);
                if (that.maxPooledPerRoute == 0) that.maxPooledPerRoute = that.connectionPoolSize;
                tcm.setDefaultMaxPerRoute(that.maxPooledPerRoute);
                cm = tcm;

            }
            else
            {
                cm = new BasicClientConnectionManager(registry);
            }
            BasicHttpParams params = new BasicHttpParams();
            if (that.socketTimeout > -1)
            {
                HttpConnectionParams.setSoTimeout(params,
                    (int) that.socketTimeoutUnits.toMillis(that.socketTimeout));

            }
            if (that.establishConnectionTimeout > -1)
            {
                HttpConnectionParams.setConnectionTimeout(params,
                    (int)that.establishConnectionTimeoutUnits.toMillis(
                        that.establishConnectionTimeout));
            }
            if (that.connectionCheckoutTimeoutMs > -1)
            {
                HttpClientParams.setConnectionManagerTimeout(params,
                    that.connectionCheckoutTimeoutMs);
            }
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, that.defaultProxy);

            return createEngine(cm, params, verifier, theContext, that.responseBufferSize);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
   protected ClientHttpEngine createEngine(ClientConnectionManager cm, BasicHttpParams params,
         X509HostnameVerifier verifier, SSLContext theContext, int responseBufferSize)
   {
      DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);
      ApacheHttpClient4Engine engine = (ApacheHttpClient4Engine) ApacheHttpClient4EngineFactory.create(httpClient,
            true);
      engine.setResponseBufferSize(responseBufferSize);
      engine.setHostnameVerifier(verifier);
      // this may be null.  We can't really support this with Apache Client.
      engine.setSslContext(theContext);
      return engine;
   }
}
