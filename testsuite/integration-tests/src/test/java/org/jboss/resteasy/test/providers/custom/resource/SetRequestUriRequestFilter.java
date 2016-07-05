package org.jboss.resteasy.test.providers.custom.resource;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;

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
