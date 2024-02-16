package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import org.junit.jupiter.api.Assertions;

public class ClientResponseFilterNullHeaderString implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        String header = responseContext.getHeaderString("header1");
        Assertions.assertNotNull(header, "The header is empty");
        Assertions.assertTrue(header.equals(""),
                "The header value doesn't match the expected one");
    }
}
