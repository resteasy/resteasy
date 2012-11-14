package org.jboss.resteasy.tests;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.SkeletonKeyToken;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;
import javax.print.DocFlavor;
import javax.ws.rs.client.WebTarget;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;


/**
 */
public class EchoTest
{
   @Test
   public void testKeyManagerFactory() throws Exception
   {
      System.out.println("alg: " + KeyManagerFactory.getDefaultAlgorithm());
   }
   /*
               <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
                <ssl name="ssl" key-alias="server" password="password"
                certificate-key-file="c:/Users/William/jboss/keystore.jks" protocol="TLSv1" verify-client="false"
                certificate-file="c:/Users/William/jboss/keystore.jks"/>
            </connector>

    */

   @Test
   public void testUser() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      client.httpEngine(new ApacheHttpClient4Engine(trustedClient(8443)));
      WebTarget appTarget = client.target("https://localhost:8443/skeleton-app");
      KeyStore idpStore = loadIDP();
      PrivateKey privateKey = (PrivateKey)idpStore.getKey("idp", "password".toCharArray());
      KeyStore clientStore = loadClientStore();
      X509Certificate clientCert = (X509Certificate)clientStore.getCertificate("client");
      System.out.println(clientCert.getSubjectX500Principal().getName());
      SkeletonKeyToken token  = new SkeletonKeyToken();
      token.principal(clientCert.getSubjectX500Principal().getName())
              .audience("MyDomain")
              .addAccess("MyService").addRole("user");

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      System.out.println(new String(tokenBytes));

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(privateKey);

      System.out.println("Key length: " + encoded.length());


      String message = appTarget.path("user/users.txt").request()
              .header("X-Skeleton-Key-Token", encoded).get(String.class);
      Assert.assertEquals("Hello User", message);

      /*
      Response response = appTarget.path("admin/admins.txt").request().get();
      Assert.assertEquals(403, response.getStatus());
      */

   }

   public static DefaultHttpClient trustedClient(int port) throws Exception
   {
      KeyStore trustStore = loadTruststore();

      KeyStore keyStore = loadClientStore();

      //SSLSocketFactory sf = new SSLSocketFactory(trustStore);
      SSLSocketFactory sf = new SSLSocketFactory(keyStore, "password", trustStore);
      X509HostnameVerifier verifier = createVerifier();
      sf.setHostnameVerifier(verifier); // only need to do because my self-generated certs aren't set up right.

      // Register our new socket factory with the typical SSL port and the
      // correct protocol name.
      Scheme httpsScheme = new Scheme("https", sf, port);
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(httpsScheme);

      HttpParams params = new BasicHttpParams();
      ClientConnectionManager cm = new SingleClientConnManager(params,
              schemeRegistry);

      return new DefaultHttpClient(cm, params);


   }

   private static KeyStore loadClientStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
   {
      KeyStore keyStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File clientFile = new File("c:/Users/William/jboss/client.jks");
      FileInputStream clientStream = new FileInputStream(clientFile);
      System.out.println("Loading client keystore from file "
              + clientFile.getPath());
      keyStore.load(clientStream, "password".toCharArray());
      System.out.println("Client keystore certificate count: "
              + keyStore.size());
      clientStream.close();
      return keyStore;
   }

   private static KeyStore loadIDP() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
   {
      KeyStore trustStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File truststoreFile = new File("c:/Users/William/jboss/idp.jks");
      FileInputStream trustStream = new FileInputStream(truststoreFile);
      System.out.println("Loading idp keystore from file "
              + truststoreFile.getPath());
      trustStore.load(trustStream, "password".toCharArray());
      trustStream.close();
      return trustStore;
   }


   private static KeyStore loadTruststore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
   {
      KeyStore trustStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File truststoreFile = new File("c:/Users/William/jboss/truststore.ts");
      FileInputStream trustStream = new FileInputStream(truststoreFile);
      System.out.println("Loading server truststore from file "
              + truststoreFile.getPath());
      trustStore.load(trustStream, "password".toCharArray());
      System.out.println("Truststore certificate count: "
              + trustStore.size());
      trustStream.close();
      return trustStore;
   }

   public static DefaultHttpClient trusted(int port) throws Exception
   {
      KeyStore trustStore = loadTruststore();

      //SSLSocketFactory sf = new SSLSocketFactory(trustStore);
      SSLSocketFactory sf = new SSLSocketFactory(null, null, trustStore);
      X509HostnameVerifier verifier = createVerifier();
      sf.setHostnameVerifier(verifier);

      // Register our new socket factory with the typical SSL port and the
      // correct protocol name.
      Scheme httpsScheme = new Scheme("https", sf, port);
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(httpsScheme);

      HttpParams params = new BasicHttpParams();
      ClientConnectionManager cm = new SingleClientConnManager(params,
              schemeRegistry);

      return new DefaultHttpClient(cm, params);


   }

   private static DefaultHttpClient selfSigned(int port)
   {
      try
      {
         java.lang.System.setProperty(
                 "sun.security.ssl.allowUnsafeRenegotiation", "true");

         // First create a trust manager that won't care.
         X509TrustManager trustManager = new X509TrustManager()
         {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException
            {
               System.out.println("**************Client");
               for (X509Certificate cert : chain)
               {
                  System.out.println("--" + cert);
               }
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException
            {
               System.out.println("*********** SERVER");
               for (X509Certificate cert : chain)
               {
                  System.out.println("--" + cert);
               }
            }

            public X509Certificate[] getAcceptedIssuers()
            {
               System.out.println("************* Accepted");
               return null;
            }
         };

         // Now put the trust manager into an SSLContext.
         // Supported: SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1
         SSLContext sslContext = SSLContext.getInstance("SSL");
         sslContext.init(null, new TrustManager[]{trustManager},
                 new SecureRandom());

         SSLSocketFactory sf = new SSLSocketFactory(sslContext);
         X509HostnameVerifier verifier = createVerifier();
         sf.setHostnameVerifier(verifier);
         // Accept any hostname, so the self-signed certificates don't fail
         //sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

         // Register our new socket factory with the typical SSL port and the
         // correct protocol name.
         Scheme httpsScheme = new Scheme("https", sf, port);
         SchemeRegistry schemeRegistry = new SchemeRegistry();
         schemeRegistry.register(httpsScheme);

         HttpParams params = new BasicHttpParams();
         ClientConnectionManager cm = new SingleClientConnManager(params,
                 schemeRegistry);

         return new DefaultHttpClient(cm, params);
      }
      catch (Exception ex)
      {

         return null;
      }
   }

   private static X509HostnameVerifier createVerifier()
   {
      return new X509HostnameVerifier() {

               @Override
               public void verify(String string, SSLSocket ssls) throws IOException
               {
               }

               @Override
               public void verify(String string, X509Certificate xc) throws SSLException
               {
               }

               @Override
               public void verify(String string, String[] strings, String[] strings1) throws SSLException {
               }

               @Override
               public boolean verify(String string, SSLSession ssls) {
                  return true;
               }
            };
   }
}

