package org.jboss.resteasy.skeleton.key.idm.service;

import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.idm.IdentityManager;
import org.jboss.resteasy.skeleton.key.idm.i18n.LogMessages;
import org.jboss.resteasy.skeleton.key.idm.i18n.Messages;
import org.jboss.resteasy.skeleton.key.idm.model.data.Realm;
import org.jboss.resteasy.skeleton.key.idm.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.idm.model.data.Resource;
import org.jboss.resteasy.skeleton.key.idm.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.ScopeMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.User;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserCredential;
import org.jboss.resteasy.skeleton.key.representations.AccessTokenResponse;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyScope;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.representations.idm.RequiredCredentialRepresentation;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/realms")
public class TokenManagement
{
   public static class AccessCode
   {
      protected String id = UUID.randomUUID().toString() + System.currentTimeMillis();
      protected long expiration;
      protected SkeletonKeyToken token;
      protected User client;

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

      public User getClient()
      {
         return client;
      }

      public void setClient(User client)
      {
         this.client = client;
      }
   }

   public TokenManagement(IdentityManager identityManager)
   {
      this.identityManager = identityManager;
   }

   protected IdentityManager identityManager;
   protected Map<String, AccessCode> accessCodeMap = new HashMap<String, AccessCode>();
   @Context
   protected UriInfo uriInfo;
   @Context
   protected Providers providers;
   @Context
   protected SecurityContext securityContext;
   @Context
   protected HttpHeaders headers;

   private static AtomicLong counter = new AtomicLong(1);
   private static String generateId()
   {
      return counter.getAndIncrement() + "." + UUID.randomUUID().toString();
   }

   protected SkeletonKeyToken createAccessToken(User user, Realm realm)
   {
      List<Resource> resources = identityManager.getResources(realm);
      SkeletonKeyToken token = new SkeletonKeyToken();
      token.id(generateId());
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
         token.setRealmAccess(access);
      }
      for (Resource resource : resources)
      {
         RoleMapping mapping = identityManager.getRoleMapping(realm, resource, user);
         if (mapping == null) continue;
         SkeletonKeyToken.Access access = token.addAccess(resource.getName())
                                               .verifyCaller(resource.isSurrogateAuthRequired());
         for (String role : mapping.getRoles())
         {
            access.addRole(role);
         }
      }
      if (token.getResourceAccess() == null || token.getResourceAccess().size() == 0) return null;
      return token;
   }

   @Path("{realm}/auth/request/login")
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
         LogMessages.LOGGER.debug(Messages.MESSAGES.realmNotFound());
         throw new NotFoundException();
      }
      if (!realm.isEnabled())
      {
         return Response.ok(Messages.MESSAGES.realmNotEnabled()).type("text/html").build();
      }
      User client = identityManager.getUser(realm, clientId);
      if (client == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.clientNotFound());
         throw new ForbiddenException();
      }
      if (!client.isEnabled())
      {
         return Response.ok(Messages.MESSAGES.requesterNotEnabled()).type("text/html").build();
      }


      String username = formData.getFirst("username");
      User user = identityManager.getUser(realm, username);
      if (user == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.userNotFound());
         return loginForm(Messages.MESSAGES.notValidUser(), redirect, clientId, scopeParam, state, realm, client);
      }
      if (!user.isEnabled())
      {
         return Response.ok(Messages.MESSAGES.accountIsNotEnabled()).type("text/html").build();

      }
      boolean authenticated = authenticate(realm, user, formData);
      if (!authenticated) return loginForm(Messages.MESSAGES.unableToAuthenticate(), redirect, clientId, scopeParam, state, realm, client);

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
      accessCode = new JWSBuilder().content(code.getId().getBytes(StandardCharsets.UTF_8)).rsa256(realm.getPrivateKey());
      UriBuilder redirectUri = UriBuilder.fromUri(redirect).queryParam("code", accessCode);
      if (state != null) redirectUri.queryParam("state", state);
      return Response.status(302).location(redirectUri.build()).build();
   }

   protected SkeletonKeyToken createToken(String scopeParam, Realm realm, User client, User user)
   {
      SkeletonKeyToken token = null;
      if (scopeParam != null)
      {
         token = new SkeletonKeyToken();
         token.id(generateId());
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
            RoleMapping roleMapping = identityManager.getRoleMapping(realm, resource, user);
            SkeletonKeyToken.Access access = token.addAccess(resource.getName());
            for (String role : scope.get(res))
            {
               if (!scopeMapping.getRoles().contains(role))
               {
                  String entity = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.knownClientNotAuthorized()+"</p>";
                  throw new ForbiddenException(Response.status(403).entity(entity).type("text/html").build());
               }
               if (!roleMapping.getRoles().contains(role))
               {
                  String entity = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.youAreNotAuthorizedForRequestedScope()+"</p>";
                  throw new ForbiddenException(Response.status(403).entity(entity).type("text/html").build());
                  
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
            String entity = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.knownClientNotAuthorizedToRequestUserLogin()+"</p>";
            throw new ForbiddenException(Response.status(403).entity(entity).type("text/html").build());
         }
         token = createAccessToken(user, realm);
      }
      return token;
   }

   @Path("{realm}/access/codes")
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
         LogMessages.LOGGER.debug(Messages.MESSAGES.codeNotSpecified());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_request");
         error.put("error_description", Messages.MESSAGES.codeNotSpecified());
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();

      }
      String client_id = formData.getFirst("client_id");
      if (client_id == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.clientIdNotSpecified());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_request");
         error.put("error_description", Messages.MESSAGES.clientIdNotSpecified());
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }
      User client = identityManager.getUser(realm, client_id);
      if (client == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.couldNotFindUser());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", Messages.MESSAGES.couldNotFindUser());
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
      }

      if (!client.isEnabled())
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.userIsNotEnabled());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", Messages.MESSAGES.userIsNotEnabled());
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
         verifiedCode = RSAProvider.verify(input, realm.getPublicKey());
      }
      catch (Exception ignored)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.failedToVerifySignature(), ignored);
      }
      if (!verifiedCode)
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", Messages.MESSAGES.unableToVerifyCodeSignature());
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      String key = input.readContent(String.class);
      AccessCode accessCode = null;
      synchronized (accessCodeMap)
      {
         accessCode = accessCodeMap.remove(key);
      }
      if (accessCode == null)
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", Messages.MESSAGES.codeNotFound());
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (accessCode.isExpired())
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", Messages.MESSAGES.codeIsExpired());
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!accessCode.getToken().isActive())
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", Messages.MESSAGES.tokenExpired());
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      if (!client.getId().equals(accessCode.getClient().getId()))
      {
         Map<String, String> res = new HashMap<String, String>();
         res.put("error", "invalid_grant");
         res.put("error_description", Messages.MESSAGES.authError());
         return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(res).build();
      }
      AccessTokenResponse res = accessTokenResponse(realm.getPrivateKey(), accessCode.getToken());
      return Response.ok(res).build();

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

   @Path("{realm}/auth/request")
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
      if (client == null) {
         String msg = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.unknownClientTryingToAccess()+"</p>";
         return Response.ok(msg).type("text/html").build();
      }
      return loginForm(null, redirect, clientId, scopeParam, state, realm, client);
   }

   private Response loginForm(String validationError, String redirect, String clientId, String scopeParam, String state, Realm realm, User client)
   {
      StringBuffer html = new StringBuffer();
      if (scopeParam != null)
      {
         html.append("<h1>").append(Messages.MESSAGES.grantRequestFor()).append(realm.getName()).append(" ").append(Messages.MESSAGES.realm()).append("</h1>");
         if (validationError != null)
         {
            try
            {
               Thread.sleep(1000); // put in a delay
            }
            catch (InterruptedException e)
            {
               throw new RuntimeException(e);
            }
            html.append("<p/><p><b>").append(validationError).append("</b></p>");
         }
         html.append("<p>").append(Messages.MESSAGES.thirdPartyIsRequestingAccess()).append("</p>");
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
            html.append("<tr><td><b>").append(Messages.MESSAGES.resource()).append("</b>").append(resource.getName()).append("</td><td><b>").append(Messages.MESSAGES.roles()).append("</b>");
            ScopeMapping mapping = identityManager.getScopeMapping(realm, resource, client);
            for (String role : scope.get(res))
            {
               html.append(" ").append(role);
               if (!mapping.getRoles().contains(role))
               {
                  String msg = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.knownClientNotAuthorized()+"</p>";
                  return Response.ok(msg).type("text/html").build();
               }
            }
            html.append("</td></tr>");
         }
         html.append("</table><p>"+Messages.MESSAGES.toAuthorizePleaseLogin()+"</p>");
      }
      else
      {
         ScopeMapping mapping = identityManager.getScopeMapping(realm, client);
         if (mapping != null && mapping.getRoles().contains("login"))
         {
            html.append("<h1>").append(Messages.MESSAGES.loginFor()).append(realm.getName()).append(" ").append(Messages.MESSAGES.realm()).append("</h1>");
            if (validationError != null)
            {
               try
               {
                  Thread.sleep(1000); // put in a delay
               }
               catch (InterruptedException e)
               {
                  throw new RuntimeException(e);
               }
               html.append("<p/><p><b>").append(validationError).append("</b></p>");
            }
         }
         else
         {
            html.append("<h1>").append(Messages.MESSAGES.grantRequestFor()).append(realm.getName()).append(" ").append(Messages.MESSAGES.realm()).append("</h1>");
            if (validationError != null)
            {
               try
               {
                  Thread.sleep(1000); // put in a delay
               }
               catch (InterruptedException e)
               {
                  throw new RuntimeException(e);
               }
               html.append("<p/><p><b>").append(validationError).append("</b></p>");
            }
            SkeletonKeyScope scope = new SkeletonKeyScope();
            List<Resource> resources = identityManager.getResources(realm);
            boolean found = false;
            for (Resource resource : resources)
            {
               ScopeMapping resourceScope = identityManager.getScopeMapping(realm, resource, client);
               if (resourceScope == null) continue;
               if (resourceScope.getRoles().size() == 0) continue;
               if (!found)
               {
                  found = true;
                  html.append("<p>").append(Messages.MESSAGES.thirdPartyIsRequestingAccess()).append("</p>");
                  html.append("<table>");
               }
               html.append("<tr><td><b>").append(Messages.MESSAGES.resource()).append("</b>").append(resource.getName()).append("</td><td><b>").append(Messages.MESSAGES.roles()).append("</b>");
               // todo add description of role
               for (String role : resourceScope.getRoles())
               {
                  html.append(" ").append(role);
                  scope.add(resource.getName(), role);
               }
            }
            if (!found)
            {
               String msg = "<h1>"+Messages.MESSAGES.securityAlert()+"</h1><p>"+Messages.MESSAGES.knownClientNotAuthorizedToAccessRealm()+"</p>";
               return Response.ok(msg).type("text/html").build();
            }
            html.append("</table>");
            try
            {
               String json = JsonSerialization.toString(scope, false);
               scopeParam = Base64Url.encode(json.getBytes(StandardCharsets.UTF_8));
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

         }
      }

      UriBuilder formActionUri = uriInfo.getBaseUriBuilder().path(TokenManagement.class).path(TokenManagement.class, "login");
      String action = formActionUri.build(realm.getId()).toString();
      html.append("<form action=\"").append(action).append("\" method=\"POST\">");
      html.append(Messages.MESSAGES.username()+"<input type=\"text\" name=\"username\" size=\"20\"><br>");
      
      for (RequiredCredential credential : identityManager.getRequiredCredentials(realm))
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
      if (scopeParam != null)
      {
         html.append("<input type=\"hidden\" name=\"scope\" value=\"").append(scopeParam).append("\">");
      }
      if (state != null) html.append("<input type=\"hidden\" name=\"state\" value=\"").append(state).append("\">");
      html.append("<input type=\"hidden\" name=\"redirect_uri\" value=\"").append(redirect).append("\">");
      html.append("<input type=\"submit\" value=\"");
      if (scopeParam == null) html.append(Messages.MESSAGES.login());
      else html.append(Messages.MESSAGES.grantAccess());
      html.append("\">");
      html.append("</form>");
      return Response.ok(html.toString()).type("text/html").build();
   }


   /**
    * OAuth Section 4.4 Client Credentials Grant
    *
    */
   @Path("{realm}/grants")
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
         LogMessages.LOGGER.debug(Messages.MESSAGES.realmIsNotEnabled());
         throw new NotFoundException();
      }

      User user = identityManager.getUser(realm, formParams.getFirst("client_id"));
      if (user == null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.couldNotFindUser());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", Messages.MESSAGES.couldNotFindUser());
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
     }

      if (!user.isEnabled())
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.userIsNotEnabled());
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "invalid_client");
         error.put("error_description", Messages.MESSAGES.userIsNotEnabled());
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
      if (token == null)
      {
         Map<String, String> error = new HashMap<String, String>();
         error.put("error", "unauthorized_client");
         return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();

      }
      return Response.ok(accessTokenResponse(realm.getPrivateKey(), token), MediaType.APPLICATION_JSON_TYPE).build();
   }

   protected boolean authenticate(Realm realm, User user, MultivaluedMap<String, String> formData)
   {
      MultivaluedMap<String, UserCredential> userCredentials = new MultivaluedHashMap<String, UserCredential>();
      List<UserCredential> creds = identityManager.getCredentials(user);
      for (UserCredential userCredential : creds)
      {
         userCredentials.add(userCredential.getType(), userCredential);
      }

      for (RequiredCredential credential : identityManager.getRequiredCredentials(realm))
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
               LogMessages.LOGGER.warn(Messages.MESSAGES.missingRequiredUserCredential());
               return false;
            }
            if (userCredential.isHashed())
            {
               value = hash(value);
            }
            if (!value.equals(userCredential.getValue()))
            {
               LogMessages.LOGGER.warn(Messages.MESSAGES.credentialMismatch());
               return false;
            }
         }
         else
         {
            if (credential.getType().equals(RequiredCredentialRepresentation.CALLER_PRINCIPAL))
            {
               List<UserCredential> principals = userCredentials.get(RequiredCredentialRepresentation.CALLER_PRINCIPAL);
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
                  LogMessages.LOGGER.warn(Messages.MESSAGES.callerPrincipalNotMatched());
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
