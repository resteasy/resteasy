package org.jboss.resteasy.test.security.testjar;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;

/**
 * ClientRequestFilter that is used to check content of attached Bearer token. If Bearer Token checks are successful the request
 * is aborted.
 */
public class ClientConfigProviderBearerTokenAbortFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        Assertions.assertEquals("Bearer myTestToken", authorizationHeader,
                "The request authorization header is not correct");
        Assertions.assertTrue(!(authorizationHeader.contains("Basic") && authorizationHeader.contains("Bearer")),
                "The request authorization header should not contain both Bearer token and Basic credentials");
        requestContext.abortWith(Response.ok().build());
    }
}
