package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class NullSetEntityTestAnnotationFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        containerResponseContext.setEntity("Hello World", new Annotation[0], MediaType.TEXT_PLAIN_TYPE);
    }
}
