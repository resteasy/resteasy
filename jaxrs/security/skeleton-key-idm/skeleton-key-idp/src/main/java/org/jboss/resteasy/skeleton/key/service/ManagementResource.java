package org.jboss.resteasy.skeleton.key.service;

import org.jboss.resteasy.skeleton.key.IdentityManager;
import org.jboss.resteasy.skeleton.key.model.data.Realm;
import org.jboss.resteasy.skeleton.key.model.data.Resource;
import org.jboss.resteasy.skeleton.key.model.data.Role;
import org.jboss.resteasy.skeleton.key.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.model.data.User;
import org.jboss.resteasy.skeleton.key.model.data.UserAttribute;
import org.jboss.resteasy.skeleton.key.model.data.UserCredential;
import org.jboss.resteasy.skeleton.key.model.representations.RealmRepresentation;
import org.jboss.resteasy.skeleton.key.model.representations.ResourceRepresentation;
import org.jboss.resteasy.skeleton.key.model.representations.RoleMappingRepresentation;
import org.jboss.resteasy.skeleton.key.model.representations.UserRepresentation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/idm")
public class ManagementResource
{
   protected IdentityManager identityManager;

   @Context
   protected UriInfo uriInfo;

   public ManagementResource(IdentityManager identityManager)
   {
      this.identityManager = identityManager;
   }

   @Path("realms")
   @POST
   @Consumes("application/json")
   public Response importDomain(RealmRepresentation rep)
   {

      if (rep.getUsers() == null)
      {
         throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                 .entity("No realm admin users defined").type("text/plain").build());
      }

      HashMap<String, UserRepresentation> userReps = new HashMap<String, UserRepresentation>();
      for (UserRepresentation userRep : rep.getUsers()) userReps.put(userRep.getUsername(), userRep);

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
                             .entity("No users declared for role mapping").type("text/plain").build());

                  }
                  if (mapping.getSurrogates() != null)
                  {
                     for (String surrogate : mapping.getSurrogates())
                     {
                        if (!userReps.containsKey(surrogate))
                        {
                           throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                   .entity("No users declared for role mapping surrogate").type("text/plain").build());
                        }
                     }
                  }
                  for (String role : mapping.getRoles())
                  {
                     if (!resourceRep.getRoles().contains(role))
                     {
                        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                .entity("No resource role for role mapping").type("text/plain").build());
                     }
                  }
               }
            }
         }
      }

      Realm realm = new Realm();
      realm.setName(rep.getRealm());
      realm.setEnabled(rep.isEnabled());
      realm.setDirectAccessTokenAllowed(rep.isDirectAccessTokenAllowed());
      realm.setTokenLifespan(rep.getTokenLifespan());
      realm = identityManager.create(realm);
      Map<String, User> userMap = new HashMap<String, User>();

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

      if (rep.getResources() != null)
      {
         for (ResourceRepresentation resourceRep : rep.getResources())
         {
            Resource resource = new Resource();
            resource.setName(resourceRep.getName());
            resource.setBaseUrl(resourceRep.getBaseUrl());
            resource.setTokenAuthRequired(resourceRep.isTokenAuthRequired());
            resource = identityManager.create(realm, resource);
            Map<String, Role> roles = new HashMap<String, Role>();
            if (resourceRep.getRoles() != null)
            {
               for (String role : resourceRep.getRoles())
               {
                  Role r = identityManager.create(realm, resource, role);
                  roles.put(r.getName(), r);
               }
            }
            if (resourceRep.getRoleMappings() != null)
            {
               for (RoleMappingRepresentation mapping : resourceRep.getRoleMappings())
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
                  for (String roleName : mapping.getRoles())
                  {
                     Role role = roles.get(roleName);
                     roleMapping.getRoles().add(role.getId());
                  }
                  identityManager.create(realm, resource, roleMapping);
               }
            }
         }
      }
      UriBuilder builder = uriInfo.getRequestUriBuilder().path(realm.getId());
      return Response.created(builder.build()).build();


   }

}
