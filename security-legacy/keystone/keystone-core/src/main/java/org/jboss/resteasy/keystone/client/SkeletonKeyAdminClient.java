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
   public ProjectsResource projects();

   @Path("/roles")
   public RolesResource roles();

   @Path("/users")
   public UsersResource users();

   @Path("/tokens")
   public TokensResource tokens();
}
