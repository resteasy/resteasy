package org.jboss.resteasy.client.jaxrs.engines;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PassthroughTrustManager implements X509TrustManager
{
   public void checkClientTrusted(X509Certificate[] chain,
                                  String authType) throws CertificateException
   {
   }

   public void checkServerTrusted(X509Certificate[] chain,
                                  String authType) throws CertificateException
   {
   }

   public X509Certificate[] getAcceptedIssuers()
   {
      return null;
   }
}
