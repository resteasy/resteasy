package org.jboss.resteasy.skeleton.key.as7.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyConfig
{
   protected String realm;
   protected String resource;

   @JsonProperty("realm-public-key")
   protected String realmKey;

   @JsonProperty("auth-url")
   protected String authUrl;
   @JsonProperty("code-url")
   protected String codeUrl;
   @JsonProperty("unsecure-cookie")
   protected boolean cookieUnsecure;
   @JsonProperty("cookie-path")
   protected String cookiePath;

   @JsonProperty("allow-any-hostname")
   protected boolean allowAnyHostname;

   protected String truststore;

   @JsonProperty("truststore-password")
   protected String truststorePassword;
   @JsonProperty("client-id")
   protected String clientId;
   @JsonProperty("client-keystore")
   protected String clientKeystore;
   @JsonProperty("client-keystore-password")
   protected String clientKeystorePassword;
   @JsonProperty("client-key-password")
   protected String clientKeyPassword;

   @JsonProperty("client-credentials")
   protected Map<String, String> clientCredentials = new HashMap<String, String>();

   @JsonProperty("connection-pool-size")
   protected int connectionPoolSize;

   @JsonProperty("ssl-not-required")
   protected boolean sslNotRequired;

   public String getRealm()
   {
      return realm;
   }

   public void setRealm(String realm)
   {
      this.realm = realm;
   }

   public String getResource()
   {
      return resource;
   }

   public void setResource(String resource)
   {
      this.resource = resource;
   }

   public String getRealmKey()
   {
      return realmKey;
   }

   public void setRealmKey(String realmKey)
   {
      this.realmKey = realmKey;
   }

   public String getAuthUrl()
   {
      return authUrl;
   }

   public void setAuthUrl(String authUrl)
   {
      this.authUrl = authUrl;
   }

   public String getCodeUrl()
   {
      return codeUrl;
   }

   public void setCodeUrl(String codeUrl)
   {
      this.codeUrl = codeUrl;
   }

   public boolean isCookieUnsecure()
   {
      return cookieUnsecure;
   }

   public void setCookieUnsecure(boolean cookieUnsecure)
   {
      this.cookieUnsecure = cookieUnsecure;
   }

   public boolean isAllowAnyHostname()
   {
      return allowAnyHostname;
   }

   public void setAllowAnyHostname(boolean allowAnyHostname)
   {
      this.allowAnyHostname = allowAnyHostname;
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

   public String getClientId()
   {
      return clientId;
   }

   public void setClientId(String clientId)
   {
      this.clientId = clientId;
   }

   public Map<String, String> getClientCredentials()
   {
      return clientCredentials;
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

   public String getCookiePath()
   {
      return cookiePath;
   }

   public void setCookiePath(String cookiePath)
   {
      this.cookiePath = cookiePath;
   }

   public int getConnectionPoolSize()
   {
      return connectionPoolSize;
   }

   public void setConnectionPoolSize(int connectionPoolSize)
   {
      this.connectionPoolSize = connectionPoolSize;
   }

   public boolean isSslNotRequired()
   {
      return sslNotRequired;
   }

   public void setSslNotRequired(boolean sslNotRequired)
   {
      this.sslNotRequired = sslNotRequired;
   }
}
