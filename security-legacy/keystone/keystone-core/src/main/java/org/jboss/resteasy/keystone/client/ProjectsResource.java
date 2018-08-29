package org.jboss.resteasy.keystone.client;

import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ProjectsResource
{
   @POST
   @Consumes("application/json")
   @Produces("application/json")
   Response create(Project project) throws Exception;

   @PUT
   @Consumes("application/json")
   @Produces("application/json")
   @Path("{id}")
   void update(@PathParam("id") String id, Project project) throws Exception;

   @DELETE
   @Path("{id}")
   Response delete(@PathParam("id") String id);


   @GET
   @Produces("application/json")
   Projects query(@QueryParam("name") String name);

   @GET
   @Path("{id}")
   @Produces("application/json")
   Project get(@PathParam("id") String id);

   @PUT
   @Path("{id}/users/{user}/roles/{role}")
   void addUserRole(@PathParam("id") String id, @PathParam("user") String userId, @PathParam("role") String roleId);

   @DELETE
   @Path("{id}/users/{user}/roles/{role}")
   void removeUserRole(@PathParam("id") String id, @PathParam("user") String userId, @PathParam("role") String roleId);
}
