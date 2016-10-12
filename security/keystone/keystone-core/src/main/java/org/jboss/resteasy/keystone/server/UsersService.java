package org.jboss.resteasy.keystone.server;

import org.infinispan.Cache;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.model.User;
import org.jboss.resteasy.util.Base64;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
@Path("/users")
@RolesAllowed("admin")
public class UsersService
{
   private Cache cache;

   public UsersService(Cache cache)
   {
      this.cache = cache;
   }

   @SuppressWarnings("unchecked")
   public String create(StoredUser user) throws Exception
   {
      String password = user.getCredentials().remove("password");
      MessageDigest digest = MessageDigest.getInstance("MD5");
      String hashPassword = Base64.encodeBytes(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
      user.getCredentials().clear();
      user.getCredentials().put("password-hash", hashPassword);
      if (user.getId() == null)
      {
         String id = UUID.randomUUID().toString();
         user.setId(id);
      }
      cache.put("/users/" + user.getId(), user, -1L, TimeUnit.MILLISECONDS);
      return user.getId();

   }

   @POST
   @Consumes("application/json")
   @Produces("application/json")
   public Response create(@Context UriInfo uriInfo, StoredUser user) throws Exception
   {
      if (!user.getCredentials().containsKey("password"))
      {
         return Response.status(Response.Status.BAD_REQUEST).entity("You did not set a password").type("text/plain").build();
      }
      String id = create(user);
      return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).entity(user.toUser()).build();
   }

   @PUT
   @Consumes("application/json")
   @Produces("application/json")
   @Path("{id}")
   @SuppressWarnings("unchecked")
   public void update(@PathParam("id") String id, StoredUser user) throws Exception
   {
      StoredUser stored = getStoredUser(id);
      if (stored == null) throw new NotFoundException();
      if (user.getName() != null) stored.setName(user.getName());
      if (user.getEnabled() != null) stored.setEnabled(user.getEnabled());
      if (user.getEmail() != null) stored.setEmail((user.getEmail()));
      if (user.getCredentials() != null && user.getCredentials().containsKey("password"))
      {
         String password = user.getCredentials().remove("password");
         MessageDigest digest = MessageDigest.getInstance("MD5");
         String hashPassword = Base64.encodeBytes(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
         stored.getCredentials().put("password", hashPassword);
      }
      cache.put("/users/" + id, stored, -1, TimeUnit.MILLISECONDS);
   }

   @DELETE
   @Path("{id}")
   public Response delete(@PathParam("id") String id)
   {
      if (cache.containsKey("/users/" + id))
      {
         cache.remove("/users/" + id);
         return Response.noContent().build();
      } else
      {
         return Response.status(Response.Status.GONE).build();
      }
   }


   @GET
   @Path("{id}")
   @Produces("application/json")
   public User get(@PathParam("id") String id)
   {
      StoredUser user = getStoredUser(id);
      if (user == null) throw new NotFoundException();

      return user.toUser();
   }

   public StoredUser getStoredUser(String id)
   {
      return (StoredUser) cache.get("/users/" + id);
   }

}
