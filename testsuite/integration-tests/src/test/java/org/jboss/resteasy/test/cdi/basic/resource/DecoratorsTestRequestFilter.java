package org.jboss.resteasy.test.cdi.basic.resource;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@DecoratorsFilterBinding
public class DecoratorsTestRequestFilter implements ContainerRequestFilter {
    @Inject
    private Logger log;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("executing DecoratorsTestRequestFilter.filter()");
    }
}
