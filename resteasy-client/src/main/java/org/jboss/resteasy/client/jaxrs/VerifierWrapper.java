package org.jboss.resteasy.client.jaxrs;

import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;

public class VerifierWrapper implements X509HostnameVerifier
{
    protected HostnameVerifier verifier;

    VerifierWrapper(HostnameVerifier verifier)
    {
        this.verifier = verifier;
    }

    @Override
    public void verify(String host, SSLSocket ssl) throws IOException
    {
        if (!verifier.verify(host, ssl.getSession())) throw new SSLException(Messages.MESSAGES.hostnameVerificationFailure());
    }

    @Override
    public void verify(String host, X509Certificate cert) throws SSLException
    {
        throw new SSLException(Messages.MESSAGES.verificationPathNotImplemented());
    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
    {
        throw new SSLException(Messages.MESSAGES.verificationPathNotImplemented());
    }

    @Override
    public boolean verify(String s, SSLSession sslSession)
    {
        return verifier.verify(s, sslSession);
    }
}
