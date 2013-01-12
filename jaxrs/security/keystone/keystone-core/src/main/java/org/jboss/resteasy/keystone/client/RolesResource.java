package org.jboss.resteasy.keystone.client;

import org.jboss.resteasy.keystone.model.Role;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface RolesResource
{
   @POST
   @Consumes("application/json")
   @Produces("application/json")
   Response create(Role role) throws Exception;

   @POST
   @Consumes("text/plain")
   @Produces("application/json")
   Response create(String roleName) throws Exception;

   @PUT
   @Consumes("application/json")
   @Produces("application/json")
   @Path("{id}")
   void update(@PathParam("id") String id, Role role) throws Exception;

   @DELETE
   @Path("{id}")
   void delete(@PathParam("id") String id);

   @GET
   @Path("{id}")
   @Produces("application/json")
   Role get(@PathParam("id") String id);
}
