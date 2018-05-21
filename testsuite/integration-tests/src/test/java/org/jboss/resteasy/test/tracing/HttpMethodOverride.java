package org.jboss.resteasy.test.tracing;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class HttpMethodOverride implements ContainerRequestFilter {
    public void filter(ContainerRequestContext ctx) throws IOException {
        String methodOverride = ctx.getHeaderString("X-Http-Method-Override");
        if (methodOverride != null) ctx.setMethod(methodOverride);
    }
}