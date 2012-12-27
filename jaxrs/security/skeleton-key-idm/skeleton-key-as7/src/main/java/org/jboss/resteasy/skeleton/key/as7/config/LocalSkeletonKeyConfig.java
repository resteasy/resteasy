package org.jboss.resteasy.skeleton.key.as7.config;

import org.bouncycastle.openssl.PEMWriter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.security.PemUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocalSkeletonKeyConfig
{
   @JsonProperty("realm")
   protected String realm;

   @JsonProperty("realm-private-key")
   protected String realmPrivateKey;

   @JsonProperty("realm-public-key")
   protected String realmPublicKey;

   @JsonProperty("access-code-expiration")
   protected int expiration;

   @JsonIgnore
   protected volatile PublicKey publicKey;
   @JsonIgnore
   protected volatile PrivateKey privateKey;


   public String getRealm()
   {
      return realm;
   }

   public void setRealm(String realm)
   {
      this.realm = realm;
   }

   public String getRealmPrivateKey()
   {
      return realmPrivateKey;
   }

   public void setRealmPrivateKey(String realmPrivateKey)
   {
      this.realmPrivateKey = realmPrivateKey;
   }

   public String getRealmPublicKey()
   {
      return realmPublicKey;
   }

   public void setRealmPublicKey(String realmPublicKey)
   {
      this.realmPublicKey = realmPublicKey;
   }

   public int getExpiration()
   {
      return expiration;
   }

   public void setExpiration(int expiration)
   {
      this.expiration = expiration;
   }

   public PublicKey getPublicKey()
   {
      if (publicKey != null) return publicKey;
      if (realmPublicKey != null)
      {
         try
         {
            publicKey = PemUtils.decodePublicKey(realmPublicKey);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      return publicKey;
   }

   public PrivateKey getPrivateKey()
   {
      if (privateKey != null) return privateKey;
      if (realmPrivateKey != null)
      {
         try
         {
            privateKey = PemUtils.decodePrivateKey(realmPrivateKey);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      return privateKey;
   }

}
