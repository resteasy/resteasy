package org.jboss.resteasy.test.providers.custom.resource;

import java.io.IOException;
import java.net.URI;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(100)
@PreMatching
public class SetRequestUriRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("https".equalsIgnoreCase(requestContext.getHeaderString("X-Forwarded-Proto"))) {
            requestContext.setRequestUri(
                    requestContext.getUriInfo().getBaseUriBuilder().scheme("https").build(),
                    requestContext.getUriInfo().getRequestUriBuilder().scheme("https").build());
        } else if (requestContext.getUriInfo().getPath().contains("setrequesturi1")) {
            requestContext.setRequestUri(
                    requestContext.getUriInfo().getRequestUriBuilder().path("uri").build());
        } else if (requestContext.getUriInfo().getPath().contains("setrequesturi2")) {
            requestContext.setRequestUri(URI.create("http://localhost:888/otherbase"),
                    URI.create("http://xx.yy:888/base/resource/sub"));
            UriInfo info = requestContext.getUriInfo();
            abortWithEntity(requestContext, info.getAbsolutePath().toASCIIString());
        }
    }

    protected void abortWithEntity(ContainerRequestContext requestContext, String entity) {
        StringBuilder sb = new StringBuilder();
        sb.append(entity);
        Response response = Response.ok(sb.toString()).build();
        requestContext.abortWith(response);
    }
}
