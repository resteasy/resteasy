package org.jboss.resteasy.skeleton.key.service;

import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.skeleton.key.IdentityManager;
import org.jboss.resteasy.skeleton.key.SkeletonKeyScope;
import org.jboss.resteasy.skeleton.key.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.model.data.Realm;
import org.jboss.resteasy.skeleton.key.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.model.data.Resource;
import org.jboss.resteasy.skeleton.key.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.model.data.ScopeMapping;
import org.jboss.resteasy.skeleton.key.model.data.User;
import org.jboss.resteasy.skeleton.key.model.data.UserCredential;
import org.jboss.resteasy.spi.ForbiddenException;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/idm")
public class TokenResource
{
   protected static class AccessCode
   {
      protected String id = UUID.randomUUID().toString() + System.currentTimeMillis();
      protected long expiration;
      protected SkeletonKeyToken token;
      protected User client;

      public boolean isExpired()
      {
         return expiration == 0 || (System.currentTimeMillis() / 1000) < expiration;
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

      public User getClient()
      {
         return client;
      }

      public void setClient(User client)
      {
         this.client = client;
      }
   }

   protected IdentityManager identityManager;
   protected Logger logger = Logger.getLogger(TokenResource.class);
   protected PrivateKey privateKey;
   protected PublicKey publicKey;
   protected Map<String, AccessCode> accessCodeMap = new HashMap<String, AccessCode>();
   @Context
   protected UriInfo uriInfo;
   @Context
   protected Providers providers;
   @Context
   protected SecurityContext securityContext;


   protected SkeletonKeyToken createAccessToken(User user, Realm realm)
   {
      List<Resource> resources = identityManager.getResources(realm);
      SkeletonKeyToken token = new SkeletonKeyToken();
      token.principal(user.getUsername());
      token.audience(realm.getName());
      if (realm.getTokenLifespan() > 0)
      {
         token.expiration((System.currentTimeMillis() / 1000) + realm.getTokenLifespan());
      }
      RoleMapping realmMapping = identityManager.getRoleMapping(realm, user);
      if (realmMapping != null && realmMapping.getRoles().size() > 0)
      {
         SkeletonKeyToken.Access access = new SkeletonKeyToken.Access();
         for (String role : realmMapping.getRoles())
         {
            access.addRole(role);
         }
         for (String surrogateId : realmMapping.getSurrogateIds())
         {
            User surrogate = identityManager.getUser(realm, surrogateId);
            access.addSurrogate(surrogate.getUsername());
         }
         token.setRealmAccess(access);
      }
      for (Resource resource : resources)
      {
         RoleMapping mapping = identityManager.getRoleMapping(realm, resource, user);
         SkeletonKeyToken.Access access = token.addAccess(resource.getName())
                                               .surrogateAuthRequired(resource.isTokenAuthRequired());
         for (String role : mapping.getRoles())
         {
            access.addRole(role);
         }
         for (String surrogateId : mapping.getSurrogateIds())
         {
            User surrogate = identityManager.getUser(realm, surrogateId);
            access.addSurrogate(surrogate.getUsername());

         }
      }
      if (token.getResourceAccess() == null || token.getResourceAccess().size() == 0) return null;
      return token;
   }

   @Path("realms/{realm}/auth/request/login")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   public Response login(@PathParam("realm") String realmName,
                         MultivaluedMap<String, String> formData)
   {
      String clientId = formData.getFirst("client_id");
      String scopeParam = formData.getFirst("scope");
      String state = formData.getFirst("state");
      String redirect = formData.getFirst("redirect_uri");

      Realm realm = identityManager.getRealm(realmName);
      if (realm == null)
      {
         logger.debug("realm not found");
         throw new NotFoundException();
      }
      if (!realm.isEnabled())
      {
         return Response.ok("Realm not enabled").type("text/html").build();
      }
      User client = identityManager.getUser(realm, clientId);
      if (client == null)
      {
         logger.debug("client not found");
         throw new ForbiddenException();
      }
      if (!client.isEnabled())
      {
         return Response.ok("Requester not enabled").type("text/html").build();
      }


      String username = formData.getFirst("username");
      User user = identityManager.getUser(realm, username);
      if (user == null)
      {
         logger.debug("user not found");
         return loginForm(true, redirect, clientId, scopeParam, realm, client);
      }
      if (!user.isEnabled())
      {
         return Response.ok("Your account is not enabled").type("text/html").build();

      }
      boolean authenticated = authenticate(realm, user, formData);
      if (!authenticated) return loginForm(true, redirect, clientId, scopeParam, realm, client);

      SkeletonKeyToken token = createToken(scopeParam, realm, client, user);
      AccessCode code = new AccessCode();
      code.setExpiration((System.currentTimeMillis() / 1000) + realm.getAccessCodeLifespan());
      code.setToken(token);
      code.setClient(client);
      synchronized (accessCodeMap)
      {
         accessCodeMap.put(code.getId(), code);
      }
      String accessCode = null;
      try
      {
         accessCode = new JWSBuilder().content(code.getId().getBytes("UTF-8")).rsa256(privateKey);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      UriBuilder redirectUri = UriBuilder.fromUri(redirect).queryParam("code", accessCode).queryParam("state", state);
      return Response.status(302).location(redirectUri.build()).build();
   }

   protected SkeletonKeyToken createToken(String scopeParam, Realm realm, User client, User user)
   {
      SkeletonKeyToken token = null;
      if (scopeParam != null)
      {
         token = new SkeletonKeyToken();
         token.principal(user.getUsername());
         token.audience(realm.getName());
         if (realm.getTokenLifespan() > 0)
         {
            token.expiration((System.currentTimeMillis() / 1000) + realm.getTokenLifespan());
         }
         SkeletonKeyScope scope = null;
         byte[] bytes = Base64Url.decode(scopeParam);
         try
         {
            scope = JsonSerialization.fromBytes(SkeletonKeyScope.class, bytes);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         for (String res : scope.keySet())
         {
            Resource resource = identityManager.getResource(realm, res);
            ScopeMapping scopeMapping = identityManager.getScopeMapping(realm, resource, client);
            RoleMapping roleMapping = identityManager.getRoleMapping(realm, user);
            SkeletonKeyToken.Access access = token.addAccess(resource.getName());
            for (String role : scope.get(res))
            {
               if (!scopeMapping.getRoles().contains(role))
               {
                  throw new ForbiddenException("Known client not authorized for the requested scope.", Response.status(403).entity("<h1>Security Alert</h1><p>Known client not authorized for the requested scope.</p>").type("text/html").build());
               }
               if (!roleMapping.getRoles().contains(role))
               {
                  throw new ForbiddenException("", Response.status(403).entity("<h1>Security Alert</h1><p>You are not authorized for the requested scope.</p>").type("text/html").build());

               }
               access.addRole(role);
               if (roleMapping.getSurrogateIds() != null && roleMapping.getSurrogateIds().size() > 0)
               {
                  throw new NotImplementedYetException(); // don't support surrogates yet
               }
            }
         }
      }
      else
      {
         ScopeMapping mapping = identityManager.getScopeMapping(realm, client);
         if (mapping == null || !mapping.getRoles().contains("login"))
         {
            throw new ForbiddenException("", Response.status(403).entity("<h1>Security Alert</h1><p>Known client not authorized to request a user login.</p>").type("text/html").build());
         }
         token = createAccessToken(user, realm);
      }
      return token;
   }

   protected void challengeResponse(String realm, String error, String description)
   {
      StringBuilder header = new StringBuilder("Bearer realm=\"").append(realm).append("\"");
      if (error != null)
      {
         header.append(", error=\"").append(error).append("\"");
      }
      if (description != null)
      {
         header.append(", error_description=\"").append(description).append("\"");
      }
      throw new NotAuthorizedException(header.toString());
   }

   @Path("realms/{realm}/access/request")
   @POST
   @Produces("application/json")
   public Response accessRequest(@PathParam("realm") String realmId,
                                 MultivaluedMap<String, String> formData)
   {
      Realm realm = identityManager.getRealm(realmId);
      if (realm == null) throw new NotFoundException();

      String code = formData.getFirst("code");
      if (code == null)
      {
         logger.debug("code not specified");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_request");
         error.put("error_description", "code not specified");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();

      }
      String client_id = formData.getFirst("client_id");
      if (client_id == null)
      {
         logger.debug("client_id not specified");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_request");
         error.put("error_description", "client_id not specified");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }
      User client = identityManager.getUser(realm, client_id);
      if (client == null)
      {
         logger.debug("Could not find user");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", "Could not find user");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }

      if (!client.isEnabled())
      {
         logger.debug("user is not enabled");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", "User is not enabled");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }

      boolean authenticated = authenticate(realm, client, formData);
      if (!authenticated)
      {
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "unauthorized_client");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }



      JWSInput input = new JWSInput(code, providers);
      boolean verifiedCode = false;
      try
      {
         verifiedCode = RSAProvider.verify(input, publicKey);
      }
      catch (Exception ignored)
      {
         logger.debug("Failed to verify signature", ignored);
      }
      if (!verifiedCode)
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Unable to verify code signature");
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      AccessCode accessCode = null;
      synchronized (accessCodeMap)
      {
         accessCode = accessCodeMap.remove(code);
      }
      if (accessCode == null || accessCode.isExpired() || !accessCode.getToken().isActive())
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Code not found or expired");
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!client.getId().equals(accessCode.getClient().getId()))
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", "Code not found or expired");
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      Map<String, String> res = accessTokenResponse(accessCode.getToken());
      return Response.ok(res).build();

   }

   protected Map<String, String> accessTokenResponse(SkeletonKeyToken token)
   {
      Map<String, String> res = new HashMap<String, String>();
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


      res.put("access_token", encodedToken);
      res.put("token_type", "bearer");
      if (token.getExpiration() != 0)
      {
         long time = token.getExpiration() - (System.currentTimeMillis() / 1000);
         res.put("expires_in", Long.toString(time));
      }
      return res;
   }

   @Path("realms/{realm}/auth/request")
   @GET
   public Response requestAccessCode(@PathParam("realm") String realmName,
                                     @QueryParam("response_type") String responseType,
                                     @QueryParam("redirect_uri") String redirect,
                                     @QueryParam("client_id") String clientId,
                                     @QueryParam("scope") String scopeParam,
                                     @QueryParam("state") String state)
   {
      Realm realm = identityManager.getRealm(realmName);
      if (realm == null) throw new NotFoundException();
      User client = identityManager.getUser(realm, clientId);
      if (client == null)
         return Response.ok("<h1>Security Alert</h1><p>Unknown client trying to get access to your account.</p>").type("text/html").build();

      return loginForm(false, redirect, clientId, scopeParam, realm, client);
   }

   private Response loginForm(boolean validationError, String redirect, String clientId, String scopeParam, Realm realm, User client)
   {
      StringBuffer html = new StringBuffer();
      if (scopeParam != null)
      {
         html.append("<h1>Grant Request For ").append(realm.getName()).append(" Realm</h1>");
         if (validationError)
         {
            try
            {
               Thread.sleep(1000); // put in a delay
            }
            catch (InterruptedException e)
            {
               throw new RuntimeException(e);
            }
            html.append("<p/><p><b>* Please recheck your username and credentials *</b></p>");
         }
         html.append("<p>A Third Party is requesting access to the following resources</p>");
         html.append("<table>");
         SkeletonKeyScope scope = null;
         byte[] bytes = Base64Url.decode(scopeParam);
         try
         {
            scope = JsonSerialization.fromBytes(SkeletonKeyScope.class, bytes);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         for (String res : scope.keySet())
         {
            Resource resource = identityManager.getResource(realm, res);
            html.append("<tr><td><B>").append(resource.getName()).append("</b></td><td>");
            ScopeMapping mapping = identityManager.getScopeMapping(realm, resource, client);
            for (String role : scope.get(res))
            {
               html.append(" ").append(role);
               if (!mapping.getRoles().contains(role))
               {
                  return Response.ok("<h1>Security Alert</h1><p>Known client not authorized for the requested scope.</p>").type("text/html").build();
               }
            }
            html.append("</td></tr>");
         }
         html.append("</table><p>To Authorize, please login below</p>");
      } else
      {
         ScopeMapping mapping = identityManager.getScopeMapping(realm, client);
         if (mapping == null || !mapping.getRoles().contains("login"))
         {
            return Response.ok("<h1>Security Alert</h1><p>Known client not authorized to request a user login.</p>").type("text/html").build();
         }
         html.append("<h1>Login For ").append(realm.getName()).append(" Realm</h1>");
      }

      UriBuilder formActionUri = uriInfo.getAbsolutePathBuilder().path("login");
      html.append("<form action=\"").append(formActionUri.build().toString()).append("\" method=\"POST\">");
      html.append("Username: <input type=\"text\" name=\"username\" size=\"20\"><br>");

      for (RequiredCredential credential : identityManager.getBrowserCredentials(realm))
      {
         if (!credential.isInput()) continue;
         html.append(credential.getType()).append(": ");
         if (credential.isSecret())
         {
            html.append("<input type=\"password\" name=\"").append(credential.getType()).append("\"  size=\"20\"><br>");

         } else
         {
            html.append("<input type=\"text\" name=\"").append(credential.getType()).append("\"  size=\"20\"><br>");
         }
      }
      html.append("<input type=\"hidden\" name=\"client_id\" value=\"").append(clientId).append("\">");
      html.append("<input type=\"hidden\" name=\"scope\" value=\"").append(scopeParam).append("\">");
      html.append("<input type=\"hidden\" name=\"state\" value=\"").append(scopeParam).append("\">");
      html.append("<input type=\"hidden\" name=\"redirect_uri\" value=\"").append(redirect).append("\">");
      html.append("</form>");
      return Response.ok(html.toString()).type("text/html").build();
   }


   /**
    * OAuth Section 4.4 Client Credentials Grant
    *
    */
   @Path("realms/{realm}/tokens")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces("application/json")
   public Response accessTokenGrant(@PathParam("realm") String realmId,
                                    MultivaluedMap<String, String> formParams)
   {
      Realm realm = identityManager.getRealm(realmId);
      if (realm == null) throw new NotFoundException();
      if (!realm.isEnabled())
      {
         logger.debug("realm is not enabled");
         throw new NotFoundException();
      }

      User user = identityManager.getUser(realm, formParams.getFirst("client_id"));
      if (user == null)
      {
         logger.debug("Could not find user");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", "Could not find user");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
     }

      if (!user.isEnabled())
      {
         logger.debug("user is not enabled");
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", "User is not enabled");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }

      boolean authenticated = authenticate(realm, user, formParams);
      if (!authenticated)
      {
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "unauthorized_client");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }
      SkeletonKeyToken token = createAccessToken(user, realm);
      return Response.ok(accessTokenResponse(token), MediaType.APPLICATION_JSON_TYPE).build();
   }

   protected boolean authenticate(Realm realm, User user, MultivaluedMap<String, String> formData)
   {
      MultivaluedMap<String, UserCredential> userCredentials = new MultivaluedHashMap<String, UserCredential>();
      List<UserCredential> creds = identityManager.getCredentials(user);
      for (UserCredential userCredential : creds)
      {
         userCredentials.add(userCredential.getType(), userCredential);
      }

      for (RequiredCredential credential : identityManager.getBrowserCredentials(realm))
      {
         if (credential.isInput())
         {
            String value = formData.getFirst(credential.getType());
            if (value == null)
            {
               return false;
            }
            UserCredential userCredential = userCredentials.getFirst(credential.getType());
            if (userCredential == null)
            {
               logger.debug("Missing required user credential");
               return false;
            }
            if (userCredential.isHashed())
            {
               value = hash(value);
            }
            if (!value.equals(userCredential.getValue()))
            {
               logger.debug("Credential mismatch");
               return false;
            }
         }
         else
         {
            if (credential.getType().equals(RequiredCredential.CALLER_PRINCIPAL))
            {
               List<UserCredential> principals = userCredentials.get(RequiredCredential.CALLER_PRINCIPAL);
               if (principals == null) return false;
               boolean found = false;
               for (UserCredential userCredential : principals)
               {
                  if (userCredential.getValue().equals(securityContext.getUserPrincipal().getName()))
                  {
                     found = true;
                     break;
                  }
               }
               if (!found)
               {
                  logger.debug("caller principal not matched");
                  return false;
               }
            }
            else // todo support other credentials
            {
               throw new NotImplementedYetException();
            }
         }
      }
      return true;
   }

   private String hash(String value)
   {
      MessageDigest digest = null;
      try
      {
         digest = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      byte[] bytes = digest.digest(value.getBytes());
      return Base64.encodeBytes(bytes);
   }

}
