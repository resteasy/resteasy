package org.jboss.resteasy.keystone.client;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public interface SkeletonKeyAdminClient
{
   @Path("/projects")
   ProjectsResource projects();

   @Path("/roles")
   RolesResource roles();

   @Path("/users")
   UsersResource users();

   @Path("/tokens")
   TokensResource tokens();
}
