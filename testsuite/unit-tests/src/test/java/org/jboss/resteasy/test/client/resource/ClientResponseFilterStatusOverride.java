package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Response;

public class ClientResponseFilterStatusOverride implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        responseContext.setStatus(Response.Status.FORBIDDEN.getStatusCode());
    }
}
