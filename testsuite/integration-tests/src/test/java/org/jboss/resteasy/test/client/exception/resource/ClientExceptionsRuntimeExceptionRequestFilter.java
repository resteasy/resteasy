package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public class ClientExceptionsRuntimeExceptionRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
        throw new RuntimeException("runtime exception");
    }
}
