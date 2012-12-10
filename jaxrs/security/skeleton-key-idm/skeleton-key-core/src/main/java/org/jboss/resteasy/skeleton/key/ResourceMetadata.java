package org.jboss.resteasy.skeleton.key;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMetadata
{
   protected String name;
   protected String realm;
   protected KeyStore keystore;
   protected KeyStore truststore;
   protected PublicKey realmKey;

   public String getName()
   {
      return name;
   }

   public String getRealm()
   {
      return realm;
   }

  /**
    * keystore that contains service's private key and certificate.
    * Used when making invocations on remote HTTPS endpoints that require client-cert authentication
    *
    * @return
    */
   public KeyStore getKeystore()
   {
      return keystore;
   }

   /**
    * Truststore to use if this service makes client invocations on remote HTTPS endpoints.
    *
    * @return
    */
   public KeyStore getTruststore()
   {
      return truststore;
   }

   /**
    * Public key of the realm.  Used to verify access tokens
    *
    * @return
    */
   public PublicKey getRealmKey()
   {
      return realmKey;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setRealm(String realm)
   {
      this.realm = realm;
   }

   public void setKeystore(KeyStore keystore)
   {
      this.keystore = keystore;
   }

   public void setTruststore(KeyStore truststore)
   {
      this.truststore = truststore;
   }

   public void setRealmKey(PublicKey realmKey)
   {
      this.realmKey = realmKey;
   }
}
