package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class ClientResponseFilterAbortWith implements ClientRequestFilter {
    private Response abortWith;

    public ClientResponseFilterAbortWith(final Response abortWith) {
        this.abortWith = abortWith;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.abortWith(abortWith);
    }
}
