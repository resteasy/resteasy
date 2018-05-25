package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyPrincipal;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.as7.config.CatalinaManagedResourceConfigLoader;
import org.jboss.resteasy.skeleton.key.as7.i18n.LogMessages;
import org.jboss.resteasy.skeleton.key.as7.i18n.Messages;
import org.jboss.resteasy.skeleton.key.config.ManagedResourceConfig;
import org.jboss.resteasy.skeleton.key.config.ManagedResourceConfigLoader;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Web deployment whose security is managed by a remote OAuth Skeleton Key authentication server
 * <p>
 * Redirects browser to remote authentication server if not logged in.  Also allows OAuth Bearer Token requests
 * that contain a Skeleton Key bearer tokens.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OAuthManagedResourceValve extends FormAuthenticator implements LifecycleListener
{
   protected RealmConfiguration realmConfiguration;
   protected UserSessionManagement userSessionManagement = new UserSessionManagement();
   protected ManagedResourceConfig remoteSkeletonKeyConfig;
   protected ResourceMetadata resourceMetadata;


   @Override
   public void start() throws LifecycleException
   {
      super.start();
      StandardContext standardContext = (StandardContext) context;
      standardContext.addLifecycleListener(this);
   }

   @Override
   public void lifecycleEvent(LifecycleEvent event)
   {
      if (event.getType() == Lifecycle.AFTER_START_EVENT) init();
   }

   protected void init()
   {
      ManagedResourceConfigLoader managedResourceConfigLoader = new CatalinaManagedResourceConfigLoader(context);
      resourceMetadata = managedResourceConfigLoader.getResourceMetadata();
      remoteSkeletonKeyConfig = managedResourceConfigLoader.getRemoteSkeletonKeyConfig();
      String client_id = remoteSkeletonKeyConfig.getClientId();
      if (client_id == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.mustSetClientId());
      }
      realmConfiguration = new RealmConfiguration();
      String authUrl = remoteSkeletonKeyConfig.getAuthUrl();
      if (authUrl == null)
      {
         throw new RuntimeException(Messages.MESSAGES.mustSpecifyAuthUrl());
      }
      String tokenUrl = remoteSkeletonKeyConfig.getCodeUrl();
      if (tokenUrl == null)
      {
         throw new RuntimeException(Messages.MESSAGES.mustSpecifyCodeUrl());
      }
      realmConfiguration.setMetadata(resourceMetadata);
      realmConfiguration.setClientId(client_id);

      for (Map.Entry<String, String> entry : managedResourceConfigLoader.getRemoteSkeletonKeyConfig().getClientCredentials().entrySet())
      {
         realmConfiguration.getCredentials().param(entry.getKey(), entry.getValue());
      }
      int size = 10;
      if (managedResourceConfigLoader.getRemoteSkeletonKeyConfig().getConnectionPoolSize() > 0)
         size = managedResourceConfigLoader.getRemoteSkeletonKeyConfig().getConnectionPoolSize();
      ResteasyClientBuilder.HostnameVerificationPolicy policy = ResteasyClientBuilder.HostnameVerificationPolicy.WILDCARD;
      if (managedResourceConfigLoader.getRemoteSkeletonKeyConfig().isAllowAnyHostname())
         policy = ResteasyClientBuilder.HostnameVerificationPolicy.ANY;
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(OAuthManagedResourceValve.class.getClassLoader());
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
              .trustStore(resourceMetadata.getTruststore())
              .keyStore(resourceMetadata.getClientKeystore(), resourceMetadata.getClientKeyPassword())
              .build();
      realmConfiguration.setClient(client);
      realmConfiguration.setAuthUrl(UriBuilder.fromUri(authUrl).queryParam("client_id", client_id));
      realmConfiguration.setCodeUrl(client.target(tokenUrl));
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      try
      {
         String requestURI = request.getDecodedRequestURI();
         if (requestURI.endsWith("j_oauth_remote_logout"))
         {
            remoteLogout(request, response);
            return;
         }
         super.invoke(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData(); // to clear push of SkeletonKeySession
      }
   }

   @Override
   public boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      try
      {
         if (bearer(false, request, response)) return true;
         else if (checkLoggedIn(request, response))
         {
            if (request.getSessionInternal().getNote(Constants.FORM_REQUEST_NOTE) != null)
            {
               if (restoreRequest(request, request.getSessionInternal()))
               {
                  LogMessages.LOGGER.debug(Messages.MESSAGES.restoreRequest());
                  return (true);
               }
               else
               {
                  LogMessages.LOGGER.debug(Messages.MESSAGES.restoreOfOriginalRequestFailed());
                  response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                  return (false);
               }
            }
            else
            {
               return true;
            }
         }

         // initiate or continue oauth2 protocol
         oauth(request, response);
      }
      catch (LoginException e)
      {
      }
      return false;
   }

   protected void remoteLogout(Request request, HttpServletResponse response) throws IOException
   {
      try
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.remoteLogout());
         if (!bearer(true, request, response))
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.bearerAuthFailed());
            return;
         }
         GenericPrincipal gp = (GenericPrincipal) request.getPrincipal();
         if (!gp.hasRole(remoteSkeletonKeyConfig.getAdminRole()))
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.roleFailure());
            response.sendError(403);
            return;
         }
         String user = request.getParameter("user");
         if (user != null)
         {
            userSessionManagement.logout(user);
         }
         else
         {
            userSessionManagement.logoutAll();
         }
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.error(Messages.MESSAGES.failedToLogout(), e);
      }
      response.setStatus(204);
   }

   protected boolean bearer(boolean challenge, Request request, HttpServletResponse response) throws LoginException, IOException
   {
      CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(realmConfiguration.getMetadata(), !remoteSkeletonKeyConfig.isCancelPropagation(), challenge);
      if (bearer.login(request, response))
      {
         return true;
      }
      return false;
   }

   protected boolean checkLoggedIn(Request request, HttpServletResponse response)
   {
      if (request.getSessionInternal() == null || request.getSessionInternal().getPrincipal() == null)
         return false;
      LogMessages.LOGGER.debug(Messages.MESSAGES.remoteLoggedInAlready());
      GenericPrincipal principal = (GenericPrincipal) request.getSessionInternal().getPrincipal();
      request.setUserPrincipal(principal);
      request.setAuthType("OAUTH");
      Session session = request.getSessionInternal();
      if (session != null && !remoteSkeletonKeyConfig.isCancelPropagation())
      {
         SkeletonKeySession skSession = (SkeletonKeySession) session.getNote(SkeletonKeySession.class.getName());
         if (skSession != null)
         {
            request.setAttribute(SkeletonKeySession.class.getName(), skSession);
            ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);

         }
      }
      return true;
   }

   /**
    * This method always set the HTTP response, so do not continue after invoking
    */
   protected void oauth(Request request, HttpServletResponse response) throws IOException
   {
      ServletOAuthLogin oauth = new ServletOAuthLogin(realmConfiguration, request, response, request.getConnector().getRedirectPort());
      String code = oauth.getCode();
      if (code == null)
      {
         String error = oauth.getError();
         if (error != null)
         {
            response.sendError(400, Messages.MESSAGES.oAuthError(error));
            return;
         }
         else
         {
            saveRequest(request, request.getSessionInternal(true));
            oauth.loginRedirect();
         }
         return;
      }
      else
      {
         if (!oauth.resolveCode(code)) return;

         SkeletonKeyToken token = oauth.getToken();
         Set<String> roles = null;
         if (resourceMetadata.getResourceName() != null)
         {
            SkeletonKeyToken.Access access = token.getResourceAccess(resourceMetadata.getResourceName());
            if (access != null) roles = access.getRoles();
         }
         else
         {
            SkeletonKeyToken.Access access = token.getRealmAccess();
            if (access != null) roles = access.getRoles();
         }
         SkeletonKeyPrincipal skp = new SkeletonKeyPrincipal(token.getPrincipal(), null);
         GenericPrincipal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), skp, roles);
         Session session = request.getSessionInternal(true);
         session.setPrincipal(principal);
         session.setAuthType("OAUTH");
         if (!remoteSkeletonKeyConfig.isCancelPropagation())
         {
            SkeletonKeySession skSession = new SkeletonKeySession(oauth.getTokenString(), realmConfiguration.getMetadata());
            session.setNote(SkeletonKeySession.class.getName(), skSession);
         }

         String username = token.getPrincipal();
         LogMessages.LOGGER.debug(Messages.MESSAGES.userSessionManageLogin(username));
         userSessionManagement.login(session, username);
      }
   }

}
