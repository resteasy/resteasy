package org.jboss.resteasy.test.cdi.basic.resource;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

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
