package org.jboss.resteasy.test.smoke;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Path("/security")
public class ProtectedResource
{

	@Path("/adminRoleCtx")
	@GET
	public String getAdminRoleCtx(@Context SecurityContext ctx)
	{
		return check(ctx.isUserInRole("admin"));
	}

	@RolesAllowed("admin")
	@Path("/adminRole")
	@GET
	public String getAdminRole()
	{
		return "Yeah";
	}

	@Path("/userRoleCtx")
	@GET
	public String getUserRoleCtx(@Context SecurityContext ctx)
	{
		return check(ctx.isUserInRole("user"));
	}

	@RolesAllowed("user")
	@Path("/userRole")
	@GET
	public String getUserRole()
	{
		return "Yeah";
	}

	@Path("/userNameCtx")
	@GET
	public String getUserNameCtx(@Context SecurityContext ctx)
	{
		return check(ctx.getUserPrincipal().getName().equals("user-name"));
	}


	@Path("/adminNameCtx")
	@GET
	public String getAdminNameCtx(@Context SecurityContext ctx)
	{
		return check(ctx.getUserPrincipal().getName().equals("admin-name"));
	}

	@Path("/authMethod")
	@GET
	public String getAuthMethod(@Context SecurityContext ctx)
	{
		return check(ctx.getAuthenticationScheme().equals("OAuth"));
	}

	private String check(boolean test) {
		System.out.println("********* IN SECURE CLIENT");
		if (!test)
		{
			System.out.println("NOT IN ROLE!!!!");
			throw new WebApplicationException(401);
		}
		return "Wild";
	}
}