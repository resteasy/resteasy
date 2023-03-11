package org.jboss.resteasy.test.tracing;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class HttpMethodOverride implements ContainerRequestFilter {
    public void filter(ContainerRequestContext ctx) throws IOException {
        String methodOverride = ctx.getHeaderString("X-Http-Method-Override");
        if (methodOverride != null)
            ctx.setMethod(methodOverride);
    }
}
