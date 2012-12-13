package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PublicKey;
import java.security.acl.Group;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyOAuthLoginModule extends JBossWebAuthLoginModule
{

   private static final ConcurrentHashMap<String, CatalinaRealmInfo> resourceMetadataCache = new ConcurrentHashMap<String, CatalinaRealmInfo>();
   protected CatalinaRealmInfo cacheEntry;
   protected SkeletonKeyTokenVerification verification;

   private static KeyStore loadKeyStore(String filename, String password) throws Exception
   {
      KeyStore trustStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File truststoreFile = new File(filename);
      FileInputStream trustStream = new FileInputStream(truststoreFile);
      trustStore.load(trustStream, password.toCharArray());
      trustStream.close();
      return trustStore;
   }


   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);
      String name = (String) options.get("resource-name");
      if (name == null) throw new RuntimeException("Must set resource-name in security domain config");
      String realm = (String) options.get("realm");
      if (realm == null) throw new RuntimeException(("Must set 'realm' in security domain config"));

      String cacheKey = realm + ":" + name;
      cacheEntry = resourceMetadataCache.get(cacheKey);
      if (cacheEntry != null) return;

      String realmKeyPem = (String) options.get("realm-public-key");
      if (realmKeyPem == null)
      {
         throw new IllegalArgumentException("You must set the realm-public-key");
      }

      PublicKey realmKey = null;
      try
      {
         realmKey = PemUtils.decodePublicKey(realmKeyPem);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      ResourceMetadata resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm(realm);
      resourceMetadata.setResourceName(name);
      resourceMetadata.setRealmKey(realmKey);


      String truststore = (String) options.get("truststore");
      if (truststore != null)
      {
         String truststorePassword = (String) options.get("truststore-password");
         KeyStore trust = null;
         try
         {
            trust = loadKeyStore(truststore, truststorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         resourceMetadata.setTruststore(trust);
      }
      String clientKeystore = (String) options.get("resource-keystore");
      String clientKeyPassword = null;
      if (clientKeystore != null)
      {
         String clientKeystorePassword = (String) options.get("resource-keystore-password");
         KeyStore serverKS = null;
         try
         {
            serverKS = loadKeyStore(clientKeystore, clientKeystorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         resourceMetadata.setClientKeystore(serverKS);
         clientKeyPassword = (String) options.get("resource-key-password");
         resourceMetadata.setClientKeyPassword(clientKeyPassword);
      }
      String client_id = (String) options.get("client-id");
      if (client_id == null)
      {
         throw new IllegalArgumentException("Must set client-id to use with auth server");
      }
      cacheEntry = new CatalinaRealmInfo();
      String authUrl = (String) options.get("auth-url");
      if (authUrl == null)
      {
         throw new RuntimeException("You must specify auth-url");
      }
      String tokenUrl = (String) options.get("token-url");
      if (tokenUrl == null)
      {
         throw new RuntimeException("You mut specify token-url");
      }
      cacheEntry.setMetadata(resourceMetadata);
      cacheEntry.setClientId(client_id);

      String credentials = (String) options.get("client-credentials");
      if (credentials != null)
      {
         String[] creds = credentials.trim().split(",");
         for (String cred : creds)
         {
            cred = cred.trim();
            if ("".equals(cred)) continue;
            String val = (String) options.get(cred);
            if (val == null) throw new RuntimeException("You must specify the credential parameter: " + cred);
            cacheEntry.getCredentials().param(cred, val);
         }
      }
      int size = 10;
      String s = (String) options.get("connection-pool-size");
      if (s != null) size = Integer.parseInt(s);
      ResteasyClient client = new ResteasyClientBuilder().connectionPoolSize(size)
              .truststore(resourceMetadata.getTruststore())
              .clientKeyStore(resourceMetadata.getClientKeystore(), clientKeyPassword)
              .build();
      cacheEntry.setClient(client);
      cacheEntry.setAuthUrl(UriBuilder.fromUri(authUrl).queryParam("client_id", client_id));
      cacheEntry.setTokenUrl(client.target(tokenUrl));
      cacheEntry.setCookiePath((String) options.get("cookie-path"));
      String secureCookie = (String) options.get("cookie-secure");
      if (secureCookie == null)
      {
         throw new RuntimeException("You must define cookie-secure.  This specifies whether security cookie should only be transmitted via HTTPS");
      }
      cacheEntry.setCookieSecure(Boolean.parseBoolean(secureCookie));
      String sslRequired = (String)options.get("ssl-required");
      if (sslRequired != null) cacheEntry.setSslRequired(Boolean.parseBoolean(sslRequired));
      resourceMetadataCache.putIfAbsent(cacheKey, cacheEntry);
   }


   @Override
   protected boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaOAuthLogin oAuthLogin = new CatalinaOAuthLogin(cacheEntry, request, response);
      try
      {
         loginOk = oAuthLogin.login();
      }
      catch (VerificationException e)
      {
         new LoginException(e.getMessage());
      }
      verification = oAuthLogin.getVerification();
      return true;
   }

   @Override
   protected Principal getIdentity()
   {
      if (verification == null) return null;
      return verification.getPrincipal();
   }

   @Override
   protected Group[] getRoleSets() throws LoginException
   {
      if (verification == null) return new Group[0];
      SimpleGroup roles = new SimpleGroup("Roles");
      Group[] roleSets = {roles};
      for (String role : verification.getRoles())
      {
         roles.addMember(new SimplePrincipal(role));
      }
      return roleSets;
   }

}
