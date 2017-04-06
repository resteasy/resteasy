package org.jboss.resteasy.skeleton.key.idm.service;

import org.jboss.resteasy.skeleton.key.idm.IdentityManager;
import org.jboss.resteasy.skeleton.key.idm.i18n.Messages;
import org.jboss.resteasy.skeleton.key.idm.model.data.Realm;
import org.jboss.resteasy.skeleton.key.idm.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.idm.model.data.Resource;
import org.jboss.resteasy.skeleton.key.idm.model.data.Role;
import org.jboss.resteasy.skeleton.key.idm.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.ScopeMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.User;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserAttribute;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserCredential;
import org.jboss.resteasy.skeleton.key.representations.idm.RealmRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.RequiredCredentialRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.ResourceRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.RoleMappingRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.ScopeMappingRepresentation;
import org.jboss.resteasy.skeleton.key.representations.idm.UserRepresentation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/realms")
public class RealmFactory
{
   protected IdentityManager identityManager;

   @Context
   protected UriInfo uriInfo;

   public RealmFactory(IdentityManager identityManager)
   {
      this.identityManager = identityManager;
   }

   @POST
   @Consumes("application/json")
   public Response importDomain(RealmRepresentation rep)
   {
      Realm realm = createRealm(rep);
      UriBuilder builder = uriInfo.getRequestUriBuilder().path(realm.getId());
      return Response.created(builder.build())
                     .entity(RealmResource.realmRep(realm, uriInfo))
                     .type(MediaType.APPLICATION_JSON_TYPE).build();
   }

   protected Realm createRealm(RealmRepresentation rep)
   {
      verifyRealmRepresentation(rep);

      Realm realm = new Realm();
      KeyPair keyPair = null;
      try
      {
         keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      realm.setPrivateKey(keyPair.getPrivate());
      realm.setPublicKey(keyPair.getPublic());
      realm.setName(rep.getRealm());
      realm.setEnabled(rep.isEnabled());
      realm.setTokenLifespan(rep.getTokenLifespan());
      realm.setAccessCodeLifespan(rep.getAccessCodeLifespan());
      realm.setSslNotRequired(rep.isSslNotRequired());
      realm = identityManager.create(realm);
      Map<String, User> userMap = new HashMap<String, User>();

      Role adminRole = identityManager.create(realm, "admin");

      for (RequiredCredentialRepresentation requiredCred : rep.getRequiredCredentials())
      {
         RequiredCredential credential = new RequiredCredential();
         credential.setType(requiredCred.getType());
         credential.setInput(requiredCred.isInput());
         credential.setSecret(requiredCred.isSecret());
         identityManager.create(realm, credential);
      }

      for (UserRepresentation userRep : rep.getUsers())
      {
         User user = new User();
         user.setUsername(userRep.getUsername());
         user.setEnabled(userRep.isEnabled());
         user = identityManager.create(realm, user);
         userMap.put(user.getUsername(), user);
         if (userRep.getCredentials() != null)
         {
            for (UserRepresentation.Credential cred : userRep.getCredentials())
            {
               UserCredential credential = new UserCredential();
               credential.setType(cred.getType());
               credential.setValue(cred.getValue());
               credential.setHashed(cred.isHashed());
               identityManager.create(user, credential);
            }
         }

         if (userRep.getAttributes() != null)
         {
            for (Map.Entry<String, String> entry : userRep.getAttributes().entrySet())
            {
               UserAttribute attribute = new UserAttribute();
               attribute.setName(entry.getKey());
               attribute.setValue(entry.getValue());
               identityManager.create(user, attribute);
            }
         }
      }

      for (RoleMappingRepresentation mapping : rep.getRoleMappings())
      {
         RoleMapping roleMapping = createRoleMapping(userMap, mapping);
         User user = userMap.get(mapping.getUsername());
         identityManager.create(realm, user, roleMapping);
      }

      for (ScopeMappingRepresentation scope : rep.getScopeMappings())
      {
         ScopeMapping scopeMapping = createScopeMapping(userMap, scope);
         User user = userMap.get(scope.getUsername());
         identityManager.create(realm, user, scopeMapping);

      }

      if (rep.getResources() != null)
      {
         for (ResourceRepresentation resourceRep : rep.getResources())
         {
            Resource resource = new Resource();
            resource.setName(resourceRep.getName());
            resource.setSurrogateAuthRequired(resourceRep.isSurrogateAuthRequired());
            resource = identityManager.create(realm, resource);
            if (resourceRep.getRoles() != null)
            {
               for (String role : resourceRep.getRoles())
               {
                  Role r = identityManager.create(realm, resource, role);
               }
            }
            if (resourceRep.getRoleMappings() != null)
            {
               for (RoleMappingRepresentation mapping : resourceRep.getRoleMappings())
               {
                  RoleMapping roleMapping = createRoleMapping(userMap, mapping);
                  User user = userMap.get(mapping.getUsername());
                  identityManager.create(realm, resource, user, roleMapping);
               }
            }
            if (resourceRep.getScopeMappings() != null)
            {
               for (ScopeMappingRepresentation mapping : resourceRep.getScopeMappings())
               {
                  ScopeMapping scopeMapping = createScopeMapping(userMap, mapping);
                  User user = userMap.get(mapping.getUsername());
                  identityManager.create(realm, resource, user, scopeMapping);
               }
            }

         }
      }
      return realm;
   }

   protected RoleMapping createRoleMapping(Map<String, User> userMap, RoleMappingRepresentation mapping)
   {
      RoleMapping roleMapping = new RoleMapping();
      User user = userMap.get(mapping.getUsername());
      roleMapping.setUserid(user.getId());
      if (mapping.getSurrogates() != null)
      {
         for (String s : mapping.getSurrogates())
         {
            User surrogate = userMap.get(s);
            roleMapping.getSurrogateIds().add(surrogate.getId());

         }
      }
      for (String role : mapping.getRoles())
      {
         roleMapping.getRoles().add(role);
      }
      return roleMapping;
   }

   protected ScopeMapping createScopeMapping(Map<String, User> userMap, ScopeMappingRepresentation mapping)
   {
      ScopeMapping scopeMapping = new ScopeMapping();
      User user = userMap.get(mapping.getUsername());
      scopeMapping.setUserid(user.getId());
      for (String role : mapping.getRoles())
      {
         scopeMapping.getRoles().add(role);
      }
      return scopeMapping;
   }


   protected void verifyRealmRepresentation(RealmRepresentation rep)
   {
      if (rep.getUsers() == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
               .entity(Messages.MESSAGES.noRealmAdminUsersDefined()).type("text/plain").build());
      }

      if (rep.getRequiredCredentials() == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
               .entity(Messages.MESSAGES.realmCredentialRequirementsNotDefined()).type("text/plain").build());

      }

      if (rep.getRoleMappings() == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
               .entity(Messages.MESSAGES.noRealmAdminUsersDefined()).type("text/plain").build());
      }

      HashMap<String, UserRepresentation> userReps = new HashMap<String, UserRepresentation>();
      for (UserRepresentation userRep : rep.getUsers()) userReps.put(userRep.getUsername(), userRep);

      // make sure there is a user that has admin privileges for the realm
      Set<UserRepresentation> admins = new HashSet<UserRepresentation>();
      for (RoleMappingRepresentation mapping : rep.getRoleMappings())
      {
         if (!userReps.containsKey(mapping.getUsername()))
         {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                  .entity(Messages.MESSAGES.noUsersDeclaredForRoleMapping()).type("text/plain").build());

         }
         for (String role : mapping.getRoles())
         {
            if (!role.equals("admin"))
            {
               throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                     .entity(Messages.MESSAGES.thereIsOnlyAdminRole()).type("text/plain").build());

            } else
            {
               admins.add(userReps.get(mapping.getUsername()));
            }
         }
      }
      if (admins.size() == 0)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
               .entity(Messages.MESSAGES.noRealmAdminUsersDefined()).type("text/plain").build());
      }

      // override enabled to false if user does not have at least all of browser or client credentials
      for (UserRepresentation userRep : rep.getUsers())
      {
         if (!userRep.isEnabled())
         {
            admins.remove(userRep);
            continue;
         }
         if (userRep.getCredentials() == null)
         {
            admins.remove(userRep);
            userRep.setEnabled(false);
         } else
         {
            boolean hasBrowserCredentials = true;
            for (RequiredCredentialRepresentation credential : rep.getRequiredCredentials())
            {
               boolean hasCredential = false;
               for (UserRepresentation.Credential cred : userRep.getCredentials())
               {
                  if (cred.getType().equals(credential.getType()))
                  {
                     hasCredential = true;
                     break;
                  }
               }
               if (!hasCredential)
               {
                  hasBrowserCredentials = false;
                  break;
               }
            }
            if (!hasBrowserCredentials)
            {
               userRep.setEnabled(false);
               admins.remove(userRep);
            }

         }
      }

      if (admins.size() == 0)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
               .entity(Messages.MESSAGES.noRealmAdminUsersEnabled()).type("text/plain").build());
      }

      if (rep.getResources() != null)
      {
         // check mappings
         for (ResourceRepresentation resourceRep : rep.getResources())
         {
            if (resourceRep.getRoleMappings() != null)
            {
               for (RoleMappingRepresentation mapping : resourceRep.getRoleMappings())
               {
                  if (!userReps.containsKey(mapping.getUsername()))
                  {
                     throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                           .entity(Messages.MESSAGES.noUsersDeclaredForRoleMapping()).type("text/plain").build());

                  }
                  if (mapping.getSurrogates() != null)
                  {
                     for (String surrogate : mapping.getSurrogates())
                     {
                        if (!userReps.containsKey(surrogate))
                        {
                           throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                 .entity(Messages.MESSAGES.noUsersDeclaredForRoleMappingSurrogate()).type("text/plain").build());
                        }
                     }
                  }
                  for (String role : mapping.getRoles())
                  {
                     if (!resourceRep.getRoles().contains(role))
                     {
                        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                              .entity(Messages.MESSAGES.noResourceRoleForRoleMapping()).type("text/plain").build());
                     }
                  }
               }
            }
         }
      }
   }

}
