package org.jboss.resteasy.test.cdi.modules.resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

/**
 * A provider packaged in a library without a {@code beans.xml}, e.g. a third-party library.
 */
@Provider
public class CDIModulesLibraryProvider implements ContainerResponseFilter {
    public static final String HEADER = "X-Library-Provider";

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        responseContext.getHeaders().putSingle(HEADER, uriInfo.getRequestUri().getPath());
    }
}
