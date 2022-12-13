package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;

public class NoContentStreamingCloseTestFilter implements ClientRequestFilter {

    private final Response response;

    public NoContentStreamingCloseTestFilter(final Response response) {
        this.response = response;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.abortWith(response);
    }
}
