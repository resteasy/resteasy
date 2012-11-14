package org.jboss.resteasy.skeleton.key;

import javax.crypto.SecretKey;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServiceMetadata
{
   protected String name;
   protected String domain;
   protected KeyStore keystore;
   protected KeyStore truststore;
   protected X509Certificate[] identityProviderCertificates;

   public String getName()
   {
      return name;
   }

   public String getDomain()
   {
      return domain;
   }

  /**
    * keystore that contains service's private key and certificate.
    * Used when making external SSL connections.
    *
    * @return
    */
   public KeyStore getKeystore()
   {
      return keystore;
   }

   /**
    * Truststore to use if this service makes external SSL connections
    *
    * @return
    */
   public KeyStore getTruststore()
   {
      return truststore;
   }

   /**
    * List of certificates used to verify signed tokens
    *
    * @return
    */
   public X509Certificate[] getIdentityProviderCertificates()
   {
      return identityProviderCertificates;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setDomain(String domain)
   {
      this.domain = domain;
   }

   public void setKeystore(KeyStore keystore)
   {
      this.keystore = keystore;
   }

   public void setTruststore(KeyStore truststore)
   {
      this.truststore = truststore;
   }

   public void setIdentityProviderCertificates(X509Certificate[] identityProviderCertificates)
   {
      this.identityProviderCertificates = identityProviderCertificates;
   }
}
