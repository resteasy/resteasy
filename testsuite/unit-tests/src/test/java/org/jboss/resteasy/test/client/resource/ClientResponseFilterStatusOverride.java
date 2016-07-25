package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class ClientResponseFilterStatusOverride implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        responseContext.setStatus(Response.Status.FORBIDDEN.getStatusCode());
    }
}
