package org.jboss.resteasy.test.security.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.xml.bind.DatatypeConverter;

/**
 * This class implements ClientRequestFilter interface. It encodes username and password and adds it to the headers.
 */
public class BasicAuthRequestFilter implements ClientRequestFilter {

    private final String token;

    public BasicAuthRequestFilter(final String user, final String password) {
        this.token = user + ":" + password;
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Authorization",
                "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8")));
    }
}
