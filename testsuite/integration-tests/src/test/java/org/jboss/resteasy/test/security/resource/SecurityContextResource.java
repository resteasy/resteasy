package org.jboss.resteasy.test.security.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("test")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class SecurityContextResource {
    @Inject
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
