package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Application;

@NameBoundProxiesAnnotation
public class NameBoundCDIProxiesInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    private static String in = "";

    /** The application context, used for retrieving the {@link ApplicationPath} value. */
    @Inject
    Application application;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object entity = application.getClass().isSynthetic() ? in + responseContext.getEntity() + "-out"
                : responseContext.getEntity();
        responseContext.setEntity(entity);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        in = "in-";
    }

}
