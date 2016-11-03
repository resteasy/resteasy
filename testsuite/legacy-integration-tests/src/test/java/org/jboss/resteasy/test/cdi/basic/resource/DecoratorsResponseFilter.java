package org.jboss.resteasy.test.cdi.basic.resource;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@DecoratorsFilterBinding
public class DecoratorsResponseFilter implements ContainerResponseFilter {
    @Inject
    private Logger log;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.info("executing DecoratorsResponseFilter.filter()");
    }
}
