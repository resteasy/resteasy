package org.jboss.resteasy.test.client.exception.resource;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class ClientExceptionsCustomExceptionRequestFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) {
        WebApplicationException exc = new ClientExceptionsCustomException("custom message");
        throw exc;
    }
}
