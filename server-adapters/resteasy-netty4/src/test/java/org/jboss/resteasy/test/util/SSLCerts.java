package org.jboss.resteasy.test.util;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Utility class for handling SSL certificates.
 *
 * @author Sebastian Łaskawiec
 */
public enum SSLCerts
{
    DEFAULT_SERVER_KEYSTORE("sni/default_server_keystore.jks", "secret".toCharArray(), null, null),
    SNI_SERVER_KEYSTORE("sni/sni_server_keystore.jks", "secret".toCharArray(), null, null),
    NO_TRUSTED_CLIENTS_KEYSTORE("sni/no_trusted_clients_keystore.jks", "secret".toCharArray(), null, null),
    DEFAULT_TRUSTSTORE(null, null, "sni/default_client_truststore.jks", "secret".toCharArray()),
    SNI_TRUSTSTORE(null, null, "sni/sni_client_truststore.jks", "secret".toCharArray());

    private final SSLContext sslContext;

    private final String keystorePath;

    private final char[] keystorePassword;

    private final String truststorePath;

    private final char[] truststorePassword;

    SSLCerts(String keystorePath, char[] keystorePassword, String truststorePath, char[] truststorePassword)
    {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.truststorePath = truststorePath;
        this.truststorePassword = truststorePassword;
        this.sslContext = getContext(fullPath(keystorePath), keystorePassword, fullPath(truststorePath),
              truststorePassword);
    }

    public static SSLContext getContext(String keyStoreFileName, char[] keyStorePassword, String trustStoreFileName,
          char[] trustStorePassword)
    {
        try
        {
            KeyManager[] keyManagers = null;
            if (keyStoreFileName != null)
            {
                KeyStore ks = KeyStore.getInstance("JKS");
                loadKeyStore(ks, keyStoreFileName, keyStorePassword);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(ks, keyStorePassword);
                keyManagers = kmf.getKeyManagers();
            }

            TrustManager[] trustManagers = null;
            if (trustStoreFileName != null)
            {
                KeyStore ks = KeyStore.getInstance("JKS");
                loadKeyStore(ks, trustStoreFileName, trustStorePassword);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
                trustManagers = tmf.getTrustManagers();
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    private static void loadKeyStore(KeyStore ks, String keyStoreFileName, char[] keyStorePassword) throws IOException,
          GeneralSecurityException
    {

        try (InputStream is = new BufferedInputStream(new FileInputStream(keyStoreFileName)))
        {
            ks.load(is, keyStorePassword);
        }
    }

    private String fullPath(String path)
    {
        if (path == null)
        {
            return null;
        }
        return SSLCerts.class.getClassLoader().getResource(path).getPath();
    }

    public SSLContext getSslContext()
    {
        return sslContext;
    }
}
