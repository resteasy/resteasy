package org.jboss.resteasy.test.cdi.ejb.resource;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * An EJB {@link Singleton @Singleton} provider which declares no scope.
 */
@Singleton
@LocalBean
@Provider
public class EjbSingletonProvider implements ContainerResponseFilter {
    public static final String HEADER = "X-Ejb-Singleton-Provider";

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        responseContext.getHeaders().putSingle(HEADER, "invoked");
    }
}
