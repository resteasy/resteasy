package org.jboss.resteasy.test.security.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.HttpResponseCodes;

@Provider
@PreMatching
public class SecurityContextContainerRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        SecurityContext securityContext = requestContext.getSecurityContext();
        if (!securityContext.isUserInRole("admin")) {
            requestContext.abortWith(Response.status(HttpResponseCodes.SC_UNAUTHORIZED)
                    .entity("User ordinaryUser is not authorized, coming from filter").build());
        }
    }
}
