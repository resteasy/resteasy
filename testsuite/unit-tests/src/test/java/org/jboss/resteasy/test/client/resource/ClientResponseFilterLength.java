package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import org.junit.jupiter.api.Assertions;

public class ClientResponseFilterLength implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        Assertions.assertEquals(10, responseContext.getLength(),
                "The length of the response is not the expected one");
    }
}
