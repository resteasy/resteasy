package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.FormAuthenticator;
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
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.skeleton.key.EnvUtil;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.as7.config.AuthServerConfig;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.security.auth.login.LoginException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Turns a web deployment into an authentication server that follwos the OAuth 2 protocol and Skeleton Key bearer tokens.
 * Authentication store is backed by a JBoss security domain.
 *
 * Servlet FORM authentication that uses the local security domain to authenticate and for role mappings.
 * <p/>
 * Supports bearer token creation and authentication.  The client asking for access must be set up as a valid user
 * within the security domain.
 * <p/>
 * If no an OAuth access request, this works like normal FORM authentication and authorization.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OAuthAuthenticationServerValve extends FormAuthenticator
{
   public static class AccessCode
   {
      protected String id = UUID.randomUUID().toString() + System.currentTimeMillis();
      protected long expiration;
      protected SkeletonKeyToken token;
      protected String client;
      protected boolean sso;

      public boolean isExpired()
      {
         return expiration != 0 && (System.currentTimeMillis() / 1000) > expiration;
      }

      public String getId()
      {
         return id;
      }

      public long getExpiration()
      {
         return expiration;
      }

      public void setExpiration(long expiration)
      {
         this.expiration = expiration;
      }

      public SkeletonKeyToken getToken()
      {
         return token;
      }

      public void setToken(SkeletonKeyToken token)
      {
         this.token = token;
      }

      public String getClient()
      {
         return client;
      }

      public void setClient(String client)
      {
         this.client = client;
      }

      public boolean isSso()
      {
         return sso;
      }

      public void setSso(boolean sso)
      {
         this.sso = sso;
      }
   }

   protected ConcurrentHashMap<String, AccessCode> accessCodeMap = new ConcurrentHashMap<String, AccessCode>();
   private static final Logger log = Logger.getLogger(OAuthAuthenticationServerValve.class);

   private static AtomicLong counter = new AtomicLong(1);

   private static String generateId()
   {
      return counter.getAndIncrement() + "." + UUID.randomUUID().toString();
   }

   protected AuthServerConfig skeletonKeyConfig;
   protected ResteasyProviderFactory providers;
   protected ResourceMetadata resourceMetadata;
   protected UserSessionManagement userSessionManagement = new UserSessionManagement();

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
   public void start() throws LifecycleException
   {
      super.start();
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      InputStream is = context.getServletContext().getResourceAsStream("/WEB-INF/resteasy-local-oauth.json");
      try
      {
         skeletonKeyConfig = mapper.readValue(is, AuthServerConfig.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      providers = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(OAuthAuthenticationServerValve.class.getClassLoader());
      try
      {
         ResteasyProviderFactory.getInstance(); // initialize builtins
         RegisterBuiltin.register(providers);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(old);
      }
      resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm(skeletonKeyConfig.getRealm());
      resourceMetadata.setRealmKey(skeletonKeyConfig.getPublicKey());
      String truststore = skeletonKeyConfig.getTruststore();
      if (truststore != null)
      {
         truststore = EnvUtil.replace(truststore);
         String truststorePassword = skeletonKeyConfig.getTruststorePassword();
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
      String clientKeystore = skeletonKeyConfig.getClientKeystore();
      String clientKeyPassword = null;
      if (clientKeystore != null)
      {
         clientKeystore = EnvUtil.replace(clientKeystore);
         String clientKeystorePassword = skeletonKeyConfig.getClientKeystorePassword();
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
         clientKeyPassword = skeletonKeyConfig.getClientKeyPassword();
         resourceMetadata.setClientKeyPassword(clientKeyPassword);
      }
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      String contextPath = request.getContextPath();
      String requestURI = request.getDecodedRequestURI();
      log.info("--- invoke: " + requestURI);
      if (request.getMethod().equalsIgnoreCase("GET")
              && context.getLoginConfig().getLoginPage().equals(request.getRequestPathMB().toString()))
      {
         if (handleLoginPage(request, response)) return;
      }
      else if (request.getMethod().equalsIgnoreCase("GET")
              && requestURI.endsWith(Actions.J_OAUTH_LOGOUT))
      {
         logoutCurrentUser(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.endsWith(Actions.J_OAUTH_ADMIN_FORCED_LOGOUT))
      {
         adminLogout(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith(Constants.FORM_ACTION)
              && request.getParameter("client_id") != null)
      {
         handleOAuth(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith(Actions.J_OAUTH_RESOLVE_ACCESS_CODE))
      {
         resolveAccessCode(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("GET")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith("j_oauth_realm_info"))
      {
         publishRealmInfo(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("GET")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith("j_oauth_realm_info.html"))
      {
         publishRealmInfoHtml(request, response);
         return;
      }
      // propagate the skeleton key token string?
      if (!skeletonKeyConfig.isCancelPropagation())
      {
         if (request.getAttribute(SkeletonKeySession.class.getName()) == null && request.getSessionInternal() != null)
         {
            SkeletonKeySession skSession = (SkeletonKeySession) request.getSessionInternal().getNote(SkeletonKeySession.class.getName());
            request.setAttribute(SkeletonKeySession.class.getName(), skSession);
            ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
         }
      }
      request.setAttribute("OAUTH_FORM_ACTION", "j_security_check");
      try
      {
         super.invoke(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();  // to clear push of SkeletonKeySession
      }
   }

   protected boolean handleLoginPage(Request request, Response response) throws IOException, ServletException
   {
      String client_id = request.getParameter("client_id");
      if (client_id == null) return false;

      String redirect_uri = request.getParameter("redirect_uri");
      String state = request.getParameter("state");

      if (redirect_uri == null || client_id == null)
      {
         response.sendError(400);
      }
      // only bypass authentication if the login query parameter is on request URL
      // and we have configured the login-role
      else if (!skeletonKeyConfig.isSsoDisabled()
              && request.getSessionInternal() != null
              && request.getSessionInternal().getPrincipal() != null
              && request.getParameter("login") != null
              && skeletonKeyConfig.getLoginRole() != null)
      {
         log.info("We're ALREADY LOGGED IN!!!");
         GenericPrincipal gp = (GenericPrincipal) request.getSessionInternal().getPrincipal();
         redirectAccessCode(true, response, redirect_uri, client_id, state, gp);
      }
      else
      {
         UriBuilder builder = UriBuilder.fromUri("j_security_check")
                 .queryParam("redirect_uri", redirect_uri)
                 .queryParam("client_id", client_id);
         if (state != null) builder.queryParam("state", state);
         String loginAction = builder.build().toString();
         request.setAttribute("OAUTH_FORM_ACTION", loginAction);
         getNext().invoke(request, response);
      }
      return true;
   }

   protected GenericPrincipal checkLoggedIn(Request request, HttpServletResponse response)
   {
      if (request.getPrincipal() != null)
      {
         return (GenericPrincipal) request.getPrincipal();
      }
      else if (request.getSessionInternal() != null && request.getSessionInternal().getPrincipal() != null)
      {
         return (GenericPrincipal) request.getSessionInternal().getPrincipal();
      }
      return null;
   }


   protected void adminLogout(Request request, HttpServletResponse response) throws IOException
   {
      log.info("<< adminLogout");
      GenericPrincipal gp = checkLoggedIn(request, response);
      if (gp == null)
      {
         if (bearer(request, response, false))
         {
            gp = (GenericPrincipal) request.getPrincipal();
         }
         else
         {
            response.sendError(403);
            return;
         }
      }
      if (!gp.hasRole(skeletonKeyConfig.getAdminRole()))
      {
         response.sendError(403);
         return;
      }
      String logoutUser = request.getParameter("user");
      if (logoutUser != null)
      {
         userSessionManagement.logout(logoutUser);
         logoutResources(logoutUser, gp.getName());
      }
      else
      {
         userSessionManagement.logoutAllBut(gp.getName());
         logoutResources(null, gp.getName());
      }
      String forwardTo = request.getParameter("forward");
      if (forwardTo == null)
      {
         response.setStatus(204);
         return;
      }
      RequestDispatcher disp =
              context.getServletContext().getRequestDispatcher(forwardTo);
      try {
         disp.forward(request.getRequest(), response);
      } catch (Throwable t) {
         request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, t);
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 "failed to forward");
      }



   }


   protected void logoutCurrentUser(Request request, HttpServletResponse response) throws IOException
   {
      if (request.getSessionInternal() == null || request.getSessionInternal().getPrincipal() == null)
      {
         redirectToWelcomePage(request, response);
         return;
      }
      GenericPrincipal principal = (GenericPrincipal) request.getSessionInternal().getPrincipal();
      String username = principal.getName();
      String admin = username;
      userSessionManagement.logout(username);
      request.setUserPrincipal(null);
      request.setAuthType(null);
      // logout user on all declared authenticated resources
      logoutResources(username, admin);
      redirectToWelcomePage(request, response);
   }

   protected void logoutResources(String username, String admin)
   {
      if (skeletonKeyConfig.getResources().size() != 0)
      {
         SkeletonKeyToken token = new SkeletonKeyToken();
         token.id(generateId());
         token.principal(admin);
         token.audience(skeletonKeyConfig.getRealm());
         SkeletonKeyToken.Access realmAccess = new SkeletonKeyToken.Access();
         realmAccess.addRole(skeletonKeyConfig.getAdminRole());
         token.setRealmAccess(realmAccess);
         String tokenString = buildTokenString(skeletonKeyConfig.getPrivateKey(), token);
         ResteasyClient client = new ResteasyClientBuilder()
                 .providerFactory(providers)
                 .hostnameVerification(AbstractClientBuilder.HostnameVerificationPolicy.ANY)
                 .truststore(resourceMetadata.getTruststore())
                 .clientKeyStore(resourceMetadata.getClientKeystore(), resourceMetadata.getClientKeyPassword())
                 .build();
         try
         {
            for (String resource : skeletonKeyConfig.getResources())
            {
               try
               {
                  log.info("logging out: " + resource);
                  WebTarget target = client.target(resource).path(Actions.J_OAUTH_REMOTE_LOGOUT);
                  if (username != null) target = target.queryParam("user", username);
                  javax.ws.rs.core.Response response = target.request()
                          .header("Authorization", "Bearer " + tokenString)
                          .put(null);
                  if (response.getStatus() != 204) log.error("Failed to log out");
                  response.close();
               }
               catch (Exception ignored)
               {
                  log.error("Failed to log out", ignored);
               }
            }
         }
         finally
         {
            client.close();
         }
      }
   }

   protected void redirectToWelcomePage(Request request, HttpServletResponse response) throws IOException
   {
      ResteasyUriInfo uriInfo = ServletUtil.extractUriInfo(request, null);
      String[] welcomes = context.findWelcomeFiles();
      if (welcomes.length > 0)
      {
         UriBuilder welcome = uriInfo.getBaseUriBuilder().path(welcomes[0]);
         response.sendRedirect(welcome.toTemplate());
      }
      else
      {
         response.setStatus(204);
      }
   }


   protected void publishRealmInfo(Request request, HttpServletResponse response) throws IOException
   {
      PublishedRealmRepresentation rep = getRealmRepresentation(request);

      response.setStatus(200);
      response.setContentType("application/json");
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      mapper.writeValue(response.getOutputStream(), rep);
      response.getOutputStream().flush();

   }

   protected void publishRealmInfoHtml(Request request, HttpServletResponse response) throws IOException
   {
      PublishedRealmRepresentation rep = getRealmRepresentation(request);

      StringBuffer html = new StringBuffer();

      html.append("<html><body><h1>Realm: ").append(rep.getRealm()).append("</h1>");
      html.append("<p>auth: ").append(rep.getAuthorizationUrl()).append("</p>");
      html.append("<p>code: ").append(rep.getCodeUrl()).append("</p>");
      html.append("<p>grant: NONE</p>");
      html.append("<p>public key: ").append(rep.getPublicKeyPem()).append("</p>");
      html.append("</body></html>");


      response.setStatus(200);
      response.setContentType("text/html");
      response.getOutputStream().println(html.toString());
      response.getOutputStream().flush();

   }


   protected PublishedRealmRepresentation getRealmRepresentation(Request request)
   {
      PublishedRealmRepresentation rep = new PublishedRealmRepresentation();
      ResteasyUriInfo uriInfo = ServletUtil.extractUriInfo(request, null);
      UriBuilder authUrl = uriInfo.getBaseUriBuilder().path(context.getLoginConfig().getLoginPage());
      UriBuilder codeUrl = uriInfo.getBaseUriBuilder().path(Actions.J_OAUTH_RESOLVE_ACCESS_CODE);
      rep.setRealm(skeletonKeyConfig.getRealm());
      rep.setPublicKeyPem(skeletonKeyConfig.getRealmPublicKey());
      rep.setAuthorizationUrl(authUrl.toTemplate());
      rep.setCodeUrl(codeUrl.toTemplate());
      return rep;
   }

   public boolean bearer(Request request, HttpServletResponse response, boolean propagate)
   {
      if (request.getHeader("Authorization") != null)
      {
         CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(false, resourceMetadata);
         try
         {
            if (bearer.login(request, response))
            {
               SkeletonKeyTokenVerification verification = bearer.getVerification();
               GenericPrincipal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
               request.setUserPrincipal(principal);
               request.setAuthType("OAUTH");
               if (propagate)
               {
                  SkeletonKeySession skSession = new SkeletonKeySession(verification.getPrincipal().getToken(), resourceMetadata);
                  request.setAttribute(SkeletonKeySession.class.getName(), skSession);
                  ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
               }
               return true;
            }
         }
         catch (LoginException e)
         {
         }
      }
      return false;
   }

   @Override
   protected void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password)
   {
      super.register(request, response, principal, authType, username, password);
      log.info("authenticate userSessionManage.login(): " + principal.getName());
      userSessionManagement.login(request.getSessionInternal(), principal.getName());
      if (!skeletonKeyConfig.isCancelPropagation())
      {
         GenericPrincipal gp = (GenericPrincipal) request.getPrincipal();
         if (gp != null)
         {
            SkeletonKeyToken token = buildToken(gp);
            String stringToken = buildTokenString(skeletonKeyConfig.getPrivateKey(), token);
            SkeletonKeySession skSession = new SkeletonKeySession(stringToken, resourceMetadata);
            request.setAttribute(SkeletonKeySession.class.getName(), skSession);
            ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
            request.getSessionInternal(true).setNote(SkeletonKeySession.class.getName(), skSession);
         }
      }
   }

   @Override
   public boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      if (bearer(request, response, true))
      {
         return true;
      }
      return super.authenticate(request, response, config);
   }


   protected void resolveAccessCode(Request request, Response response) throws IOException
   {
      // always verify code and remove access code from map before authenticating user
      // if user authentication fails, we want the code to be removed irreguardless just in case we're under attack
      String code = request.getParameter("code");
      JWSInput input = new JWSInput(code, providers);
      boolean verifiedCode = false;
      try
      {
         verifiedCode = RSAProvider.verify(input, skeletonKeyConfig.getPublicKey());
      }
      catch (Exception ignored)
      {
         log.debug("Failed to verify signature", ignored);
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      if (!verifiedCode)
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Unable to verify code signature");
         response.sendError(400);
         response.setContentType("application/json");
         mapper.writeValue(response.getOutputStream(), res);
         response.getOutputStream().flush();
         return;
      }
      String key = input.readContent(String.class);
      AccessCode accessCode = accessCodeMap.remove(key);

      String username = request.getParameter("client_id");
      String password = request.getParameter("password");
      GenericPrincipal gp = (GenericPrincipal) context.getRealm().authenticate(username, password);
      if (gp == null)
      {
         log.error("Failed to authenticate client_id");
         response.sendError(400);
         return;
      }
      if (accessCode == null)
      {
         log.error("No access code: " + code);
         response.sendError(400);
         return;
      }
      if (accessCode.isExpired())
      {
         log.error("Access code expired");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Code is expired");
         response.setStatus(400);
         response.setContentType("application/json");
         mapper.writeValue(response.getOutputStream(), res);
         response.getOutputStream().flush();
         return;
      }
      if (!accessCode.getToken().isActive())
      {
         log.error("token not active");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Token expired");
         response.setStatus(400);
         response.setContentType("application/json");
         mapper.writeValue(response.getOutputStream(), res);
         response.getOutputStream().flush();
         return;
      }
      if (!username.equals(accessCode.getClient()))
      {
         log.error("not equal client");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Auth error");
         response.setStatus(400);
         response.setContentType("application/json");
         mapper.writeValue(response.getOutputStream(), res);
         response.getOutputStream().flush();
         return;
      }
      if (accessCode.isSso() && (skeletonKeyConfig.getLoginRole() == null || !gp.hasRole(skeletonKeyConfig.getLoginRole())))
      {
         // we did not authenticate user on an access code request because a session was already established
         // but, the client_id does not have permission to bypass this on a simple grant.  We want
         // to always ask for credentials from a simple oath request

         log.error("does not have login role");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Auth error");
         response.setStatus(400);
         response.setContentType("application/json");
         mapper.writeValue(response.getOutputStream(), res);
         response.getOutputStream().flush();
         return;
      }
      String wildcard = skeletonKeyConfig.getWildcardRole() == null ? "*" : skeletonKeyConfig.getWildcardRole();
      // is we have a login role, then we don't need to filter out roles, just grant all the roles the user has
      // Also, if the client has the "wildcard" role, then we don't need to filter out roles
      if (accessCode.getToken().getRealmAccess().getRoles() != null
              && !gp.hasRole(wildcard)
              && (skeletonKeyConfig.getLoginRole() == null || !gp.hasRole(skeletonKeyConfig.getLoginRole())))
      {
         Set<String> clientAllowed = new HashSet<String>();
         for (String role : gp.getRoles())
         {
            clientAllowed.add(role);
         }
         Set<String> newRoles = new HashSet<String>();
         newRoles.addAll(accessCode.getToken().getRealmAccess().getRoles());
         for (String role : newRoles)
         {
            if (!clientAllowed.contains(role))
            {
               accessCode.getToken().getRealmAccess().getRoles().remove(role);
            }
         }
      }
      AccessTokenResponse res = accessTokenResponse(skeletonKeyConfig.getPrivateKey(), accessCode.getToken());
      response.setStatus(200);
      response.setContentType("application/json");
      mapper.writeValue(response.getOutputStream(), res);
      response.getOutputStream().flush();
      return;
   }

   protected AccessTokenResponse accessTokenResponse(PrivateKey privateKey, SkeletonKeyToken token)
   {
      String encodedToken = buildTokenString(privateKey, token);

      AccessTokenResponse res = new AccessTokenResponse();
      res.setToken(encodedToken);
      res.setTokenType("bearer");
      if (token.getExpiration() != 0)
      {
         long time = token.getExpiration() - (System.currentTimeMillis() / 1000);
         res.setExpiresIn(time);
      }
      return res;
   }

   protected String buildTokenString(PrivateKey privateKey, SkeletonKeyToken token)
   {
      byte[] tokenBytes = null;
      try
      {
         tokenBytes = JsonSerialization.toByteArray(token, false);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      return new JWSBuilder()
              .content(tokenBytes)
              .rsa256(privateKey);
   }


   protected void handleOAuth(Request request, Response response) throws IOException
   {
      log.info("<--- Begin oauthAuthenticate");
      String redirect_uri = request.getParameter("redirect_uri");
      String client_id = request.getParameter("client_id");
      String state = request.getParameter("state");
      String username = request.getParameter(Constants.FORM_USERNAME);
      String password = request.getParameter(Constants.FORM_PASSWORD);
      Principal principal = context.getRealm().authenticate(username, password);
      if (principal == null)
      {
         forwardToErrorPage(request, response, context.getLoginConfig());
         return;
      }
      GenericPrincipal gp = (GenericPrincipal) principal;
      register(request, response, principal, HttpServletRequest.FORM_AUTH, username, password);
      userSessionManagement.login(request.getSessionInternal(), username);
      redirectAccessCode(false, response, redirect_uri, client_id, state, gp);

      return;
   }

   protected void redirectAccessCode(boolean sso, Response response, String redirect_uri, String client_id, String state, GenericPrincipal gp) throws IOException
   {
      SkeletonKeyToken token = buildToken(gp);
      AccessCode code = new AccessCode();
      code.setToken(token);
      code.setClient(client_id);
      code.setSso(sso);
      int expiration = skeletonKeyConfig.getExpiration() == 0 ? 300 : skeletonKeyConfig.getExpiration();
      code.setExpiration((System.currentTimeMillis() / 1000) + expiration);
      accessCodeMap.put(code.getId(), code);
      log.info("--- sign access code");
      String accessCode = null;
      try
      {
         accessCode = new JWSBuilder().content(code.getId().getBytes("UTF-8")).rsa256(skeletonKeyConfig.getPrivateKey());
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      log.info("--- build redirect");
      UriBuilder redirectUri = UriBuilder.fromUri(redirect_uri).queryParam("code", accessCode);
      if (state != null) redirectUri.queryParam("state", state);
      response.sendRedirect(redirectUri.toTemplate());
      log.info("<--- end oauthAuthenticate");
   }

   protected SkeletonKeyToken buildToken(GenericPrincipal gp)
   {
      SkeletonKeyToken token = new SkeletonKeyToken();
      token.id(generateId());
      token.principal(gp.getName());
      token.audience(skeletonKeyConfig.getRealm());
      SkeletonKeyToken.Access realmAccess = new SkeletonKeyToken.Access();
      for (String role : gp.getRoles())
      {
         realmAccess.addRole(role);
      }
      token.setRealmAccess(realmAccess);
      return token;
   }

}
