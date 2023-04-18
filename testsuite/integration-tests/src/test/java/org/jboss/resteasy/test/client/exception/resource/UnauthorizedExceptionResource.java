package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.spi.HttpResponseCodes;

public class UnauthorizedExceptionResource implements UnauthorizedExceptionInterface {
    public void postIt(String msg) {
        throw new WebApplicationException(HttpResponseCodes.SC_UNAUTHORIZED);
    }
}
