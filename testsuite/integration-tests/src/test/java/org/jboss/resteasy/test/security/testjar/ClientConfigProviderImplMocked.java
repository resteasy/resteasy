package org.jboss.resteasy.test.security.testjar;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigException;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.test.security.resource.CustomTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding SSLContext.
 */
public class ClientConfigProviderImplMocked implements ClientConfigProvider {
    static String KEYSTORE_PATH = null;

    @Override
    public String getUsername(URI uri) throws ClientConfigException {
        return null;
    }

    @Override
    public String getPassword(URI uri) throws ClientConfigException {
        return null;
    }

    @Override
    public String getBearerToken(URI uri) throws ClientConfigException {
        return null;
    }

    @Override
    public SSLContext getSSLContext(URI uri) throws ClientConfigException {
        SSLContext sslContext;
        if (KEYSTORE_PATH != null) {
            try {
                KeyStore correctTruststore = KeyStore.getInstance("jks");
                try (InputStream in = new FileInputStream(KEYSTORE_PATH)) {
                    correctTruststore.load(in, "123456".toCharArray());
                }
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new CustomTrustManager(correctTruststore)}, null);
            } catch (Exception e) {
                throw new ClientConfigException(e);
            }
        } else {
            try {
                sslContext = SSLContext.getDefault();
            } catch (NoSuchAlgorithmException e) {
                throw new ClientConfigException(e);
            }
        }

        return sslContext;
    }
}
