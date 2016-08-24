package org.jboss.resteasy.test.security.resource;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("test")
public class SecurityContextResource {
    @Context
    SecurityContext securityContext;

    @GET
    @Produces("text/plain")
    public String get() {
        if (!securityContext.isUserInRole("admin")) {
            throw new WebApplicationException(Response.serverError().status(HttpResponseCodes.SC_UNAUTHORIZED)
                    .entity("User " + securityContext.getUserPrincipal().getName() + " is not authorized").build());
        }
        return "Good user " + securityContext.getUserPrincipal().getName();
    }
}
