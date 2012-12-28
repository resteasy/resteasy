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
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.as7.config.LocalSkeletonKeyConfig;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.representations.idm.PublishedRealmRepresentation;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servlet FORM authentication that uses the local security domain to authenticate and for role mappings.
 *
 * Supports bearer token creation and authentication.  The client asking for access must be set up as a valid user
 * within the security domain.
 *
 * If no an OAuth access request, this works like normal FORM authentication and authorization.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocalOAuthAuthenticatorValve extends FormAuthenticator
{
   public static class AccessCode
   {
      protected String id = UUID.randomUUID().toString() + System.currentTimeMillis();
      protected long expiration;
      protected SkeletonKeyToken token;
      protected String client;

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
   }

   protected Map<String, AccessCode> accessCodeMap = new HashMap<String, AccessCode>();
   private static final Logger log = Logger.getLogger(LocalOAuthAuthenticatorValve.class);

   private static AtomicLong counter = new AtomicLong(1);

   private static String generateId()
   {
      return counter.getAndIncrement() + "." + UUID.randomUUID().toString();
   }

   protected LocalSkeletonKeyConfig skeletonKeyConfig;
   protected ResteasyProviderFactory providers;
   private ResourceMetadata resourceMetadata;

   @Override
   public void start() throws LifecycleException
   {
      super.start();
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("resteasy-local-oauth.json");
      try
      {
         skeletonKeyConfig = mapper.readValue(is, LocalSkeletonKeyConfig.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      providers = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(SkeletonKeyOAuthLoginModule.class.getClassLoader());
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
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      String contextPath = request.getContextPath();
      String requestURI = request.getDecodedRequestURI();
      if (request.getMethod().equalsIgnoreCase("GET")
              && context.getLoginConfig().getLoginPage().equals(request.getRequestPathMB().toString()))
      {
         String client_id = request.getParameter("client_id");
         if (client_id != null)
         {
            String redirect_uri = request.getParameter("redirect_uri");
            String state = request.getParameter("state");
            if (redirect_uri == null || client_id == null)
            {
               response.sendError(400);
               return;
            }
            UriBuilder builder = UriBuilder.fromUri("j_security_check")
                    .queryParam("redirect_uri", redirect_uri)
                    .queryParam("client_id", client_id);
            if (state != null) builder.queryParam("state", state);
            String loginAction = builder.build().toString();
            request.setAttribute("OAUTH_FORM_ACTION", loginAction);
            getNext().invoke(request, response);
            return;
         }
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith(Constants.FORM_ACTION)
              && request.getParameter("client_id") != null)
      {
         oauthAuthenticate(request, response);
         return;
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith("j_oauth_resolve_access_code"))
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
      // default behavior
      request.setAttribute("OAUTH_FORM_ACTION", "j_security_check");
      super.invoke(request, response);
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
      UriBuilder codeUrl = uriInfo.getBaseUriBuilder().path("j_oauth_resolve_access_code");
      rep.setRealm(skeletonKeyConfig.getRealm());
      rep.setPublicKeyPem(skeletonKeyConfig.getRealmPublicKey());
      rep.setAuthorizationUrl(authUrl.toTemplate());
      rep.setCodeUrl(codeUrl.toTemplate());
      return rep;
   }


   @Override
   public boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      if (request.getHeader("Authorization") != null)
      {
         CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(false, resourceMetadata);
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
         }
         return false;
      }
      return super.authenticate(request, response, config);
   }


   protected void resolveAccessCode(Request request, Response response) throws IOException
   {
      String username = request.getParameter("client_id");
      String password = request.getParameter("password");
      String code = request.getParameter("code");
      GenericPrincipal gp = (GenericPrincipal) context.getRealm().authenticate(username, password);
      if (gp == null)
      {
         log.error("Failed to authenticate client_id");
         response.sendError(400);
         return;
      }
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
      if (!verifiedCode)
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Unable to verify code signature");
         response.sendError(400);
         //return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      String key = input.readContent(String.class);
      AccessCode accessCode = null;
      synchronized (accessCodeMap)
      {
         accessCode = accessCodeMap.remove(key);
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
         response.sendError(400);
         return;
         //return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!accessCode.getToken().isActive())
      {
         log.error("token not active");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Token expired");
         response.sendError(400);
         return;
//            return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!username.equals(accessCode.getClient()))
      {
         log.error("not equal client");
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Auth error");
         response.sendError(400);
         return;
//            return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (accessCode.getToken().getRealmAccess().getRoles() != null)
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
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      mapper.writeValue(response.getOutputStream(), res);
      response.getOutputStream().flush();
      return;
   }

   protected AccessTokenResponse accessTokenResponse(PrivateKey privateKey, SkeletonKeyToken token)
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
      String encodedToken = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(privateKey);

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


   protected void oauthAuthenticate(Request request, Response response) throws IOException
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
      AccessCode code = new AccessCode();
      code.setToken(token);
      code.setClient(client_id);
      int expiration = skeletonKeyConfig.getExpiration() == 0 ? 300 : skeletonKeyConfig.getExpiration();
      code.setExpiration((System.currentTimeMillis() / 1000) + expiration);
      synchronized (accessCodeMap)
      {
         accessCodeMap.put(code.getId(), code);
      }
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

      return;
   }

}
