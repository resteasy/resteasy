package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.as7.config.LocalSkeletonKeyConfig;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;

import javax.servlet.ServletException;
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
 * Uses the local security domain as a authentication server for OAuth.
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

   private static AtomicLong counter = new AtomicLong(1);

   private static String generateId()
   {
      return counter.getAndIncrement() + "." + UUID.randomUUID().toString();
   }

   protected LocalSkeletonKeyConfig skeletonKeyConfig;

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
   }

   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      String contextPath = request.getContextPath();
      String requestURI = request.getDecodedRequestURI();
      if (request.getMethod().equalsIgnoreCase("GET")
              && context.getLoginConfig().getLoginPage().equals(request.getRequestPathMB().toString()))
      {
         String loginAction = Constants.FORM_ACTION;
         String response_type = request.getParameter("response_type");
         if (response_type != null && response_type.equals("code"))
         {
            String redirect_uri = request.getParameter("redirect_uri");
            String client_id = request.getParameter("client_id");
            String state = request.getParameter("state");
            if (redirect_uri == null || client_id == null || state == "null")
            {
               response.sendError(400);
               return;
            }
            UriBuilder builder = UriBuilder.fromUri(Constants.FORM_ACTION)
                    .queryParam("redirect_uri", redirect_uri)
                    .queryParam("client_id", client_id)
                    .queryParam("state", state);
            loginAction = builder.build().toString();
         }
         request.setAttribute("OAUTH_FORM_ACTION", loginAction);
         getNext().invoke(request, response);
      }
      else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith(Constants.FORM_ACTION)
              && request.getParameter("client_id") != null)
      {
         oauthAuthenticate(request, response);
      } else if (request.getMethod().equalsIgnoreCase("POST")
              && requestURI.startsWith(contextPath) &&
              requestURI.endsWith("j_oauth_resolve_access_code"))
      {
         resolveAccessCode(request, response);
      }
      else
      {
         // its just a regular form login
         super.invoke(request, response);
      }
   }

   protected void resolveAccessCode(Request request, Response response) throws IOException
   {
      String username = request.getParameter("client_id");
      String password = request.getParameter("password");
      String code = request.getParameter("code");
      GenericPrincipal gp = (GenericPrincipal) context.getRealm().authenticate(username, password);
      if (gp == null)
      {
         response.sendError(400);
         return;
      }
      AccessCode accessCode = null;
      synchronized (accessCodeMap)
      {
         accessCode = accessCodeMap.remove(code);
      }
      if (accessCode == null)
      {
         response.sendError(400);
         return;
      }
      if (accessCode.isExpired())
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Code is expired");
         response.sendError(400);
         return;
         //return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!accessCode.getToken().isActive())
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Token expired");
         response.sendError(400);
         return;
//            return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!username.equals(accessCode.getClient()))
      {
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
      token.principal(gp.getName());
      token.audience(skeletonKeyConfig.getRealm());
      SkeletonKeyToken.Access realmAccess = new SkeletonKeyToken.Access();
      for (String role : gp.getRoles())
      {
         realmAccess.addRole(role);
      }
      AccessCode code = new AccessCode();
      code.setToken(token);
      code.setClient(client_id);
      int expiration = skeletonKeyConfig.getExpiration() == 0 ? 300 : skeletonKeyConfig.getExpiration();
      code.setExpiration((System.currentTimeMillis() / 1000) + expiration);
      synchronized (accessCodeMap)
      {
         accessCodeMap.put(code.getId(), code);
      }
      String accessCode = null;
      try
      {
         accessCode = new JWSBuilder().content(code.getId().getBytes("UTF-8")).rsa256(skeletonKeyConfig.getPrivateKey());
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      UriBuilder redirectUri = UriBuilder.fromUri(redirect_uri).queryParam("code", accessCode);
      if (state != null) redirectUri.queryParam("state", state);
      response.sendRedirect(redirectUri.toTemplate());
      return;
   }

}
