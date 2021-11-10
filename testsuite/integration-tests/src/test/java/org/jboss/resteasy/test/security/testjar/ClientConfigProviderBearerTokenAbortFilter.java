package org.jboss.resteasy.test.security.testjar;

import org.junit.Assert;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

/**
 * ClientRequestFilter that is used to check content of attached Bearer token. If Bearer Token checks are successful the request is aborted.
 */
public class ClientConfigProviderBearerTokenAbortFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        Assert.assertEquals("The request authorization header is not correct", "Bearer myTestToken", authorizationHeader);
        Assert.assertTrue("The request authorization header should not contain both Bearer token and Basic credentials", !(authorizationHeader.contains("Basic") && authorizationHeader.contains("Bearer")));
        requestContext.abortWith(Response.ok().build());
    }
}
