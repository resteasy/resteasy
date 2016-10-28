package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.client.proxy.ClientResponseFailureTest;

import javax.ws.rs.core.Response;

public class ClientResponseFailureResource implements ClientResponseFailureTest.ClientResponseFailureResourceInterface {
    public String get() {
        return "hello world";
    }

    public String error() {
        Response r = Response.status(404).type("text/plain").entity("there was an error").build();
        throw new NoLogWebApplicationException(r);
    }
}
