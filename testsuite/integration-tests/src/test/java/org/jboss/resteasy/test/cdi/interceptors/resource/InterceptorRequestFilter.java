package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@InterceptorFilterBinding
@InterceptorRequestFilterInterceptorBinding
public class InterceptorRequestFilter implements ContainerRequestFilter {
    @Inject
    private Logger log;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("executing InterceptorRequestFilter.filter()");
    }
}
