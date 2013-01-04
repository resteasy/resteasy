package org.jboss.resteasy.skeleton.key.as7.config;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.security.PemUtils;

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

   @JsonProperty("cancel-propagation")
   protected boolean cancelPropagation;

   protected boolean ssoDisabled;

   @JsonIgnore
   protected volatile PublicKey publicKey;
   @JsonIgnore
   protected volatile PrivateKey privateKey;

   // these properties are optional and used to provide connection metadata when the server wants to make
   // remote SSL connections

   protected String truststore;
   @JsonProperty("truststore-password")
   protected String truststorePassword;
   @JsonProperty("client-keystore")
   protected String clientKeystore;
   @JsonProperty("client-keystore-password")
   protected String clientKeystorePassword;
   @JsonProperty("client-key-password")
   protected String clientKeyPassword;


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

   public String getTruststore()
   {
      return truststore;
   }

   public void setTruststore(String truststore)
   {
      this.truststore = truststore;
   }

   public String getTruststorePassword()
   {
      return truststorePassword;
   }

   public void setTruststorePassword(String truststorePassword)
   {
      this.truststorePassword = truststorePassword;
   }

   public String getClientKeystore()
   {
      return clientKeystore;
   }

   public void setClientKeystore(String clientKeystore)
   {
      this.clientKeystore = clientKeystore;
   }

   public String getClientKeystorePassword()
   {
      return clientKeystorePassword;
   }

   public void setClientKeystorePassword(String clientKeystorePassword)
   {
      this.clientKeystorePassword = clientKeystorePassword;
   }

   public String getClientKeyPassword()
   {
      return clientKeyPassword;
   }

   public void setClientKeyPassword(String clientKeyPassword)
   {
      this.clientKeyPassword = clientKeyPassword;
   }

   public boolean isCancelPropagation()
   {
      return cancelPropagation;
   }

   public void setCancelPropagation(boolean cancelPropagation)
   {
      this.cancelPropagation = cancelPropagation;
   }

   public boolean isSsoDisabled()
   {
      return ssoDisabled;
   }

   public void setSsoDisabled(boolean ssoDisabled)
   {
      this.ssoDisabled = ssoDisabled;
   }
}
