package org.jboss.resteasy.test.client.exception.resource;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.WebApplicationException;

public class UnauthorizedExceptionResource implements UnauthorizedExceptionInterface {
    public void postIt(String msg) {
        throw new WebApplicationException(HttpResponseCodes.SC_UNAUTHORIZED);
    }
}
