package org.jboss.resteasy.plugins.interceptors;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(Priorities.AUTHORIZATION)
public class RoleBasedSecurityFilter implements ContainerRequestFilter {
    protected String[] rolesAllowed;
    protected boolean denyAll;
    protected boolean permitAll;

    public RoleBasedSecurityFilter(final String[] rolesAllowed, final boolean denyAll, final boolean permitAll) {
        this.rolesAllowed = rolesAllowed;
        this.denyAll = denyAll;
        this.permitAll = permitAll;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (denyAll) {
            throw new ForbiddenException(
                    Response.status(403).entity("Access forbidden: role not allowed").type("text/html;charset=UTF-8").build());
        }
        if (permitAll)
            return;
        if (rolesAllowed != null) {
            SecurityContext context = ResteasyContext.getContextData(SecurityContext.class);
            if (context != null) {
                for (String role : rolesAllowed) {
                    if (context.isUserInRole(role))
                        return;
                }
                throw new ForbiddenException(Response.status(403).entity("Access forbidden: role not allowed")
                        .type("text/plain;charset=UTF-8").build());
            }
        }
        return;
    }
}
