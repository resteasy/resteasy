package org.jboss.resteasy.spring.web;

import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class ResponseStatusContainerResponseFilter implements ContainerResponseFilter {

    private final int defaultStatusCode;
    private final int newStatusCode;

    public ResponseStatusContainerResponseFilter(final int newStatusCode) {
        this(HttpResponseCodes.SC_OK, newStatusCode);
    }

    public ResponseStatusContainerResponseFilter(final int defaultStatusCode, final int newStatusCode) {
        this.defaultStatusCode = defaultStatusCode;
        this.newStatusCode = newStatusCode;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() == defaultStatusCode) { // only set the status if it has not already been set
            responseContext.setStatus(newStatusCode);
        }
    }
}
