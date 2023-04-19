package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;

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
