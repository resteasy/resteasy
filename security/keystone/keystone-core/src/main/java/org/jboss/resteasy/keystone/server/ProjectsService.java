package org.jboss.resteasy.keystone.server;

import org.infinispan.Cache;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.Roles;
import org.jboss.resteasy.keystone.model.StoredProject;
import org.jboss.resteasy.keystone.model.User;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/projects")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProjectsService
{
   private Cache cache;
   private UsersService usersResource;
   private RolesService rolesResource;

   @Context
   private UriInfo uriInfo;

   public ProjectsService(Cache cache, UsersService usersResource, RolesService rolesResource)
   {
      this.cache = cache;
      this.usersResource = usersResource;
      this.rolesResource = rolesResource;
   }

   public void createProject(Project project) throws Exception
   {
      if (project.getId() == null)
      {
         String id = UUID.randomUUID().toString();
         project.setId(id);
      }
      StoredProject storedProject = new StoredProject(project);
      cache.put(projectCacheId(project.getId()), storedProject, -1L, TimeUnit.MILLISECONDS);

   }

   @GET
   @Produces("application/json")
   public Projects getProjects(@QueryParam("name") String name)
   {
      Projects projects = new Projects();
      List<Project> list = new ArrayList<Project>();
      projects.setList(list);
      for (Object key : cache.keySet())
      {
         if (!(key instanceof String)) continue;
         if (!key.toString().startsWith("/projects/")) continue;
         StoredProject stored = (StoredProject) cache.get(key);
         if (stored == null) throw new NotFoundException();
         if (name == null || name.equals(stored.getProject().getName()))
         {
            list.add(stored.getProject());
         }
      }
      return projects;
   }

   @POST
   @Consumes("application/json")
   @Produces("application/json")
   @RolesAllowed("admin")
   public Response create(Project project) throws Exception
   {
      createProject(project);
      return Response.created(uriInfo.getAbsolutePathBuilder().path(project.getId()).build()).build();
   }

   @PUT
   @Consumes("application/json")
   @Produces("application/json")
   @Path("{id}")
   @RolesAllowed("admin")
   public void update(@PathParam("id") String id, Project project) throws Exception
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(id));
      if (storedProject == null) throw new NotFoundException();
      Project stored = (Project) storedProject.getProject();
      if (project.getName() != null) stored.setName(project.getName());
      if (project.getDescription() != null) stored.setDescription(project.getDescription());
      if (project.getEnabled() != null) stored.setEnabled(project.getEnabled());
      cache.put(projectCacheId(id), storedProject, -1, TimeUnit.MILLISECONDS);
   }

   @DELETE
   @Path("{id}")
   @RolesAllowed("admin")
   public Response delete(@PathParam("id") String id)
   {
      if (cache.containsKey(projectCacheId(id)))
      {
         cache.remove(projectCacheId(id));
         return Response.noContent().build();
      } else
      {
         return Response.status(Response.Status.GONE).build();
      }
   }

   public static String projectCacheId(String id)
   {
      return "/projects/" + id;
   }


   @GET
   @Path("{id}")
   @Produces("application/json")
   public Project getProject(@PathParam("id") String id)
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(id));
      if (storedProject == null) throw new NotFoundException();
      return storedProject.getProject();
   }

   public String getUserIdByName(String projectId, String username)
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(projectId));
      if (storedProject == null) throw new NotFoundException();
      return storedProject.getUserNameIds().get(username);
   }

   public Roles getUserRoles(String id, String userId)
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(id));
      Set<String> roleMapping = storedProject.roleMapping(userId);
      Roles roles = new Roles();
      if (roleMapping == null)
      {
         return roles;
      } else
      {
         for (String roleId : roleMapping)
         {
            Role role = rolesResource.get(roleId);
            if (role != null)
            {
               roles.getRoles().add(role);
            }
         }
         return roles;
      }
   }


   @PUT
   @Path("{id}/users/{user}/roles/{role}")
   @RolesAllowed("admin")
   public void addUserRole(@PathParam("id") String id, @PathParam("user") String userId, @PathParam("role") String roleId)
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(id));
      if (storedProject == null) throw new NotFoundException();
      User user = usersResource.get(userId);
      Role role = rolesResource.get(roleId);
      storedProject.addUserRoleMapping(user, role);
      cache.put(projectCacheId(id), storedProject, -1, TimeUnit.MILLISECONDS);
   }

   @DELETE
   @Path("{id}/users/{user}/roles/{role}")
   @RolesAllowed("admin")
   public void removeUserRole(@PathParam("id") String id, @PathParam("user") String userId, @PathParam("role") String roleId)
   {
      StoredProject storedProject = (StoredProject)cache.get(projectCacheId(id));
      if (storedProject == null) throw new NotFoundException();
      User user = usersResource.get(userId);
      Role role = rolesResource.get(roleId);
      storedProject.removeUserRoleMapping(user, role);
      cache.put(projectCacheId(id), storedProject, -1, TimeUnit.MILLISECONDS);
   }
}
