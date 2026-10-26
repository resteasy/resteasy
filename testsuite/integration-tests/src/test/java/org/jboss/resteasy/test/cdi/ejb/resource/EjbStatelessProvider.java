package org.jboss.resteasy.test.cdi.ejb.resource;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * A {@link Stateless @Stateless} provider which declares no scope.
 */
@Stateless
@LocalBean
@Provider
public class EjbStatelessProvider implements ContainerResponseFilter {
    public static final String HEADER = "X-Ejb-Stateless-Provider";

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        responseContext.getHeaders().putSingle(HEADER, "invoked");
    }
}
