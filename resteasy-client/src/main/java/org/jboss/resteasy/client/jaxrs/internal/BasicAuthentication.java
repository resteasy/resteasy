package org.jboss.resteasy.client.jaxrs.internal;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.util.BasicAuthHelper;

/**
 * Client filter that will do basic authentication. You must allocate it and then register it with the Client or WebTarget
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthentication implements ClientRequestFilter {
    private final String authHeader;

    /**
     *
     * @param username user name
     * @param password password
     */
    public BasicAuthentication(final String username, final String password) {
        authHeader = BasicAuthHelper.createHeader(username, password);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authHeader);
    }
}
