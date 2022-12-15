package org.jboss.resteasy.client.jaxrs.engines;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PassthroughTrustManager extends X509ExtendedTrustManager {
    public void checkClientTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {

    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {

    }
}
