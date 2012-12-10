package org.jboss.resteasy.skeleton.key;

import java.security.KeyStore;
import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyPrincipal implements Principal
{
   protected String name;
   protected String surrogate;
   protected String token;
   protected KeyStore keystore;
   protected KeyStore truststore;

   public SkeletonKeyPrincipal(String name, String surrogate, String token, KeyStore keystore, KeyStore truststore)
   {
      this.name = name;
      this.surrogate = surrogate;
      this.token = token;
      this.keystore = keystore;
      this.truststore = truststore;
   }

   @Override
   public String getName()
   {
      return name;
   }

   public String getSurrogate()
   {
      return surrogate;
   }

   /**
    * On the wire string representation of token
    *
    * @return
    */
   public String getToken()
   {
      return token;
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
}
