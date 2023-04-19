package org.jboss.resteasy.test.client.exception.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

public class ClientExceptionsCustomExceptionResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext)
            throws IOException {
        WebApplicationException exc = new ClientExceptionsCustomException("custom message");
        throw exc;
    }
}
