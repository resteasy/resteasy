package org.jboss.resteasy.test.client.exception.resource;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class ClientExceptionsIOExceptionRequestFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        throw new IOException("client io");
    }
}
