package org.jboss.resteasy.test.security.resource;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.CertificateException;

public class CustomTrustManager implements X509TrustManager {

   private X509TrustManager defaultTrustManager;

   public CustomTrustManager(final KeyStore truststore) throws NoSuchAlgorithmException, KeyStoreException {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(truststore);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      for (TrustManager trustManager : trustManagers) {
         if (trustManager instanceof X509TrustManager) {
            defaultTrustManager = (X509TrustManager) trustManager;
            return;
         }
      }
   }

   @Override
   public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

   }

   public void checkServerTrusted(X509Certificate[]chain, String authType) throws CertificateException {
      defaultTrustManager.checkServerTrusted(chain, authType);
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
   }
}
