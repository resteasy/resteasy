package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.EnvUtil;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.as7.config.RemoteSkeletonKeyConfig;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
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
 * Uses a configured remote auth server to do OAuth2 or Bearer token authentication.  SkeletonKeyTokens are used
 * to provide user data and role mappings.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RemoteOAuthAuthenticatorValve extends AbstractRemoteOAuthAuthenticatorValve
{
   protected CatalinaRealmConfiguration realmConfiguration;

   @Override
   public void start() throws LifecycleException
   {
      super.start();
      String client_id = remoteSkeletonKeyConfig.getClientId();
      if (client_id == null)
      {
         throw new IllegalArgumentException("Must set client-id to use with auth server");
      }
      realmConfiguration = new CatalinaRealmConfiguration();
      String authUrl = remoteSkeletonKeyConfig.getAuthUrl();
      if (authUrl == null)
      {
         throw new RuntimeException("You must specify auth-url");
      }
      String tokenUrl = remoteSkeletonKeyConfig.getCodeUrl();
      if (tokenUrl == null)
      {
         throw new RuntimeException("You mut specify code-url");
      }
      realmConfiguration.setMetadata(resourceMetadata);
      realmConfiguration.setClientId(client_id);

      for (Map.Entry<String, String> entry : remoteSkeletonKeyConfig.getClientCredentials().entrySet())
      {
         realmConfiguration.getCredentials().param(entry.getKey(), entry.getValue());
      }
      int size = 10;
      if (remoteSkeletonKeyConfig.getConnectionPoolSize() > 0) size = remoteSkeletonKeyConfig.getConnectionPoolSize();
      AbstractClientBuilder.HostnameVerificationPolicy policy = AbstractClientBuilder.HostnameVerificationPolicy.WILDCARD;
      if (remoteSkeletonKeyConfig.isAllowAnyHostname()) policy = AbstractClientBuilder.HostnameVerificationPolicy.ANY;
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(SkeletonKeyOAuthLoginModule.class.getClassLoader());
      try
      {
         ResteasyProviderFactory.getInstance(); // initialize builtins
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
              .clientKeyStore(resourceMetadata.getClientKeystore(), resourceMetadata.getClientKeyPassword())
              .build();
      realmConfiguration.setClient(client);
      realmConfiguration.setAuthUrl(UriBuilder.fromUri(authUrl).queryParam("client_id", client_id));
      realmConfiguration.setCodeUrl(client.target(tokenUrl));
      realmConfiguration.setCookiePath(remoteSkeletonKeyConfig.getCookiePath());
      realmConfiguration.setCookieSecure(!remoteSkeletonKeyConfig.isCookieUnsecure());
      realmConfiguration.setSslRequired(!remoteSkeletonKeyConfig.isSslNotRequired());
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      try
      {
         super.invoke(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData(); // to clear push of SkeletonKeySession
      }
   }

   @Override
   protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      try
      {
         if (bearer(request, response)) return true;
         else if (oauth(request, response)) return true;
      }
      catch (LoginException e)
      {
      }
      return false;
   }

   protected boolean bearer(Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(false, realmConfiguration.getMetadata());
      if (bearer.login(request, response))
      {
         SkeletonKeyTokenVerification verification = bearer.getVerification();
         Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
         request.setUserPrincipal(principal);
         if (!remoteSkeletonKeyConfig.isCancelPropagation())
         {
            SkeletonKeySession skSession = new SkeletonKeySession(verification.getPrincipal().getToken(), realmConfiguration.getMetadata());
            request.setAttribute(SkeletonKeySession.class.getName(), skSession);
            ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
         }
         return true;
      }
      return false;
   }

   protected boolean oauth(Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaOAuthAuthenticator oauth = new CatalinaOAuthAuthenticator(realmConfiguration);
      if (oauth.login(request, response))
      {
         SkeletonKeyTokenVerification verification = oauth.getVerification();
         Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
         request.setUserPrincipal(principal);
         SkeletonKeySession skSession = new SkeletonKeySession(verification.getPrincipal().getToken(), realmConfiguration.getMetadata());
         request.setAttribute(SkeletonKeySession.class.getName(), skSession);
         ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
         return true;
      }
      return false;
   }

}
