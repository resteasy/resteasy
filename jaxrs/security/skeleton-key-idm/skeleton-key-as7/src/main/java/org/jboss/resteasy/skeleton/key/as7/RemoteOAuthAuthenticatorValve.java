package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.EnvUtil;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyPrincipal;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uses a configured remote auth server to do OAuth2 or Bearer token authentication.  SkeletonKeyTokens are used
 * to provide user data and role mappings.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RemoteOAuthAuthenticatorValve extends AbstractRemoteOAuthAuthenticatorValve implements SessionListener
{
   protected RealmConfiguration realmConfiguration;
   private static final Logger log = Logger.getLogger(RemoteOAuthAuthenticatorValve.class);
   protected ConcurrentHashMap<String, Map<String, Session>> userSessionMap = new ConcurrentHashMap<String, Map<String, Session>>();


   @Override
   public void start() throws LifecycleException
   {
      super.start();
      String client_id = remoteSkeletonKeyConfig.getClientId();
      if (client_id == null)
      {
         throw new IllegalArgumentException("Must set client-id to use with auth server");
      }
      realmConfiguration = new RealmConfiguration();
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
      Thread.currentThread().setContextClassLoader(RemoteOAuthAuthenticatorValve.class.getClassLoader());
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
      realmConfiguration.setSslRequired(!remoteSkeletonKeyConfig.isSslNotRequired());
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      String contextPath = request.getContextPath();
      String requestURI = request.getDecodedRequestURI();
      if (requestURI.endsWith("j_oauth_remote_logout"))
      {
         remoteLogout(request, response);
         return;
      }
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
         if (bearer(false, request, response)) return true;
         else if (checkLoggedIn(request, response)) return true;
         else if (oauth(request, response)) return true;
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
         log.info("->> remoteLogout: ");
         if (!bearer(true, request, response))
         {
            log.info("remoteLogout: bearer auth failed");
            return;
         }
         GenericPrincipal gp = (GenericPrincipal)request.getPrincipal();
         if (!gp.hasRole(remoteSkeletonKeyConfig.getAdminRole()))
         {
            log.info("remoteLogout: role failure");
            response.sendError(403);
            return;
         }
         String user = request.getParameter("user");
         if (user != null)
         {
            logoutUser(user);
         }
         else
         {
            ArrayList<String> users = new ArrayList<String>();
            users.addAll(userSessionMap.keySet());
            for (String u :users) logoutUser(user);
         }
      }
      catch (Exception e)
      {
         log.error("failed to logout", e);
      }
      response.setStatus(204);
   }

   protected void logoutUser(String user)
   {
      log.info("logoutUser: " + user);
      Map<String, Session> map = userSessionMap.remove(user);
      if (map == null)
      {
         log.info("no session for user");
         return;
      }
      synchronized (map)
      {
         for (Session session : map.values())
         {
            log.info("invalidating session");
            session.setPrincipal(null);
            session.setAuthType(null);
            session.getSession().invalidate();
         }
      }
   }

   protected boolean bearer(boolean challenge, Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(challenge, realmConfiguration.getMetadata());
      if (bearer.login(request, response))
      {
         SkeletonKeyTokenVerification verification = bearer.getVerification();
         Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
         request.setUserPrincipal(principal);
         request.setAuthType("OAUTH");
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

   protected boolean checkLoggedIn(Request request, HttpServletResponse response)
   {
      if (cache && request.getSessionInternal() == null || request.getSessionInternal().getPrincipal() == null)
         return false;
      log.info("remote logged in already");
      GenericPrincipal principal = (GenericPrincipal) request.getSessionInternal().getPrincipal();
      bindRequest(request, principal);
      return true;
   }

   protected void bindRequest(Request request, GenericPrincipal principal)
   {
      request.setUserPrincipal(principal);
      request.setAuthType("OAUTH");
      SkeletonKeyPrincipal skp = (SkeletonKeyPrincipal) principal.getUserPrincipal();
      SkeletonKeySession skSession = new SkeletonKeySession(skp.getToken(), realmConfiguration.getMetadata());
      request.setAttribute(SkeletonKeySession.class.getName(), skSession);
      ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
   }

   protected boolean oauth(Request request, HttpServletResponse response) throws LoginException
   {
      CatalinaOAuthLogin oauth = new CatalinaOAuthLogin(realmConfiguration, request, response);
      if (oauth.login())
      {
         SkeletonKeyTokenVerification verification = oauth.getVerification();
         GenericPrincipal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
         Session session = request.getSessionInternal(true);
         session.setPrincipal(principal);
         session.setAuthType("OAUTH");

         String username = verification.getPrincipal().getName();
         Map<String, Session> map = userSessionMap.get(username);
         if (map == null)
         {
            final Map<String, Session> value = new HashMap<String, Session>();
            map = userSessionMap.putIfAbsent(username, value);
            if (map == null)
            {
               map = value;
            }
         }
         synchronized (map)
         {
            map.put(session.getId(), session);
         }

         if (oauth.isCodePresent()) // redirect without code
         {
            StringBuffer buf = request.getRequestURL().append("?").append(request.getQueryString());
            UriBuilder builder = UriBuilder.fromUri(buf.toString()).replaceQueryParam("code", null);
            try
            {
               String location = builder.build().toString();
               log.info("* redirect to stripped query parameters: " + location);
               response.sendRedirect(location);
               return false;
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }

         bindRequest(request, principal);
         return true;
      }
      return false;
   }

   @Override
   /**
    * Clean up user-sesssion relationships
    *
    */
   public void sessionEvent(SessionEvent event)
   {
      // We only care about session destroyed events
      if (!Session.SESSION_DESTROYED_EVENT.equals(event.getType())
              && (!Session.SESSION_PASSIVATED_EVENT.equals(event.getType())))
         return;

      // Look up the single session id associated with this session (if any)
      Session session = event.getSession();
      GenericPrincipal principal = (GenericPrincipal) session.getPrincipal();
      if (principal == null) return;
      session.setPrincipal(null);
      session.setAuthType(null);

      String username = principal.getUserPrincipal().getName();
      Map<String, Session> map = userSessionMap.get(username);
      if (map == null) return;
      synchronized (map)
      {
         map.remove(session.getId());
         if (map.isEmpty()) userSessionMap.remove(username);
      }


   }
}
