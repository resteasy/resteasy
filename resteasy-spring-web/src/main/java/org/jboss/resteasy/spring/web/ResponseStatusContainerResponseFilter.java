package org.jboss.resteasy.spring.web;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class ResponseStatusContainerResponseFilter implements ContainerResponseFilter {

    private final int status;

    public ResponseStatusContainerResponseFilter(final int status) {
        this.status = status;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.setStatus(status);
    }
}
