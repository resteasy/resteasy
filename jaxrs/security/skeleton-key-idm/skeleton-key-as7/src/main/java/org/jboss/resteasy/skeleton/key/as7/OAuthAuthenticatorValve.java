package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.as7.config.SkeletonKeyConfig;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OAuthAuthenticatorValve extends AuthenticatorBase
{
   protected CatalinaRealmConfiguration realmConfiguration;

   @Override
   public void start() throws LifecycleException
   {
      super.start();
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("resteasy-oauth.json");
      SkeletonKeyConfig config = null;
      try
      {
         config = mapper.readValue(is, SkeletonKeyConfig.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      String name = config.getResource();
      String realm = config.getRealm();
      if (realm == null) throw new RuntimeException("Must set 'realm' in config");

      String realmKeyPem = config.getRealmKey();
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


      String truststore = config.getTruststore();
      if (truststore != null)
      {
         String truststorePassword = config.getTruststorePassword();
         KeyStore trust = null;
         try
         {
            trust = loadKeyStore(truststore, truststorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to load truststore", e);
         }
         resourceMetadata.setTruststore(trust);
      }
      String clientKeystore = config.getClientKeystore();
      String clientKeyPassword = null;
      if (clientKeystore != null)
      {
         String clientKeystorePassword = config.getClientKeystorePassword();
         KeyStore serverKS = null;
         try
         {
            serverKS = loadKeyStore(clientKeystore, clientKeystorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to load keystore", e);
         }
         resourceMetadata.setClientKeystore(serverKS);
         clientKeyPassword = config.getClientKeyPassword();
         resourceMetadata.setClientKeyPassword(clientKeyPassword);
      }
      String client_id = config.getClientId();
      if (client_id == null)
      {
         throw new IllegalArgumentException("Must set client-id to use with auth server");
      }
      realmConfiguration = new CatalinaRealmConfiguration();
      String authUrl = config.getAuthUrl();
      if (authUrl == null)
      {
         throw new RuntimeException("You must specify auth-url");
      }
      String tokenUrl = config.getCodeUrl();
      if (tokenUrl == null)
      {
         throw new RuntimeException("You mut specify code-url");
      }
      realmConfiguration.setMetadata(resourceMetadata);
      realmConfiguration.setClientId(client_id);

      for (Map.Entry<String, String> entry : config.getClientCredentials().entrySet())
      {
         realmConfiguration.getCredentials().param(entry.getKey(), entry.getValue());
      }
      int size = 10;
      if (config.getConnectionPoolSize() > 0) size = config.getConnectionPoolSize();
      AbstractClientBuilder.HostnameVerificationPolicy policy = AbstractClientBuilder.HostnameVerificationPolicy.WILDCARD;
      if (config.isAllowAnyHostname()) policy = AbstractClientBuilder.HostnameVerificationPolicy.ANY;
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(SkeletonKeyOAuthLoginModule.class.getClassLoader());
      try
      {
         RegisterBuiltin.register(providerFactory);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(old);
      }
      ResteasyClient client = new ResteasyClientBuilder()
              .providerFactory(providerFactory)
              .connectionPoolSize(size)
              .hostnameVerification(policy)
              .truststore(resourceMetadata.getTruststore())
              .clientKeyStore(resourceMetadata.getClientKeystore(), clientKeyPassword)
              .build();
      realmConfiguration.setClient(client);
      realmConfiguration.setAuthUrl(UriBuilder.fromUri(authUrl).queryParam("client_id", client_id));
      realmConfiguration.setCodeUrl(client.target(tokenUrl));
      realmConfiguration.setCookiePath(config.getCookiePath());
      realmConfiguration.setCookieSecure(!config.isCookieUnsecure());
      realmConfiguration.setSslRequired(!config.isSslNotRequired());
   }

   @Override
   protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(false, realmConfiguration.getMetadata());
      try
      {
         if (bearer.login(request, response))
         {
            SkeletonKeyTokenVerification verification = bearer.getVerification();
            Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
            request.setUserPrincipal(principal);
            return true;
         }
      }
      catch (LoginException e)
      {
         return false;
      }
      CatalinaOAuthAuthenticator oauth = new CatalinaOAuthAuthenticator(realmConfiguration);
      try
      {
         if (oauth.login(request, response))
         {
            SkeletonKeyTokenVerification verification = oauth.getVerification();
            Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
            request.setUserPrincipal(principal);
            return true;
         }
      }
      catch (LoginException e)
      {
         return false;
      }
      return false;
   }

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

}
