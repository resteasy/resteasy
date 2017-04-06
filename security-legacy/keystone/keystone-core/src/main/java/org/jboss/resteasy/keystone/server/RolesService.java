package org.jboss.resteasy.keystone.server;

import org.infinispan.Cache;
import org.jboss.resteasy.keystone.model.Role;

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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/roles")
@RolesAllowed("admin")
@SuppressWarnings({"rawtypes", "unchecked"})
public class RolesService
{
   private Cache cache;

   public RolesService(Cache cache)
   {
      this.cache = cache;
   }

   public void create(Role role) throws Exception
   {
      if (role.getId() == null)
      {
         String id = UUID.randomUUID().toString();
         role.setId(id);
      }
      cache.put(roleCacheEntry(role.getId()), role, -1L, TimeUnit.MILLISECONDS);
   }

   @POST
   @Consumes("application/json")
   @Produces("application/json")
   public Response create(@Context UriInfo uriInfo, Role role) throws Exception
   {
      create(role);
      return Response.created(uriInfo.getAbsolutePathBuilder().path(role.getId()).build()).entity(role).build();
   }

   @POST
   @Consumes("text/plain")
   @Produces("application/json")
   public Response create(@Context UriInfo uriInfo, String roleName) throws Exception
   {
      Role role = new Role();
      role.setName(roleName);
      create(role);
      return Response.created(uriInfo.getAbsolutePathBuilder().path(role.getId()).build()).entity(role).build();
   }


   @PUT
   @Consumes("application/json")
   @Produces("application/json")
   @Path("{id}")
   public void update(@PathParam("id") String id, Role role) throws Exception
   {
      Role storedRole = (Role) cache.get(roleCacheEntry(id));
      if (storedRole == null) throw new NotFoundException();
      if (role.getName() != null) storedRole.setName(role.getName());
      cache.put(roleCacheEntry(id), storedRole, -1, TimeUnit.MILLISECONDS);
   }

   @DELETE
   @Path("{id}")
   public Response delete(@PathParam("id") String id)
   {
      if (cache.containsKey(roleCacheEntry(id)))
      {
         cache.remove(roleCacheEntry(id));
         return Response.noContent().build();
      } else
      {
         return Response.status(Response.Status.GONE).build();
      }
   }

   public static String roleCacheEntry(String id)
   {
      return "/roles/" + id;
   }


   @GET
   @Path("{id}")
   @Produces("application/json")
   public Role get(@PathParam("id") String id)
   {
      Role storedRole = (Role) cache.get(roleCacheEntry(id));
      if (storedRole == null) throw new NotFoundException();
      return storedRole;
   }

}
