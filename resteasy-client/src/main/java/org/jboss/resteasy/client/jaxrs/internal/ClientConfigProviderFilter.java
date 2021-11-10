package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.util.BasicAuthHelper;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URI;

/**
 * Client filter that will attach authorization header with either HTTP Basic auth or Bearer token auth.
 * Credentials and token are loaded from ClientConfigProvider implementation.
 *
 * @author dvilkola@redhat.com
 */
public class ClientConfigProviderFilter implements ClientRequestFilter {

    private final ClientConfigProvider clientConfigProvider;

    public ClientConfigProviderFilter(final ClientConfigProvider clientConfigProvider) {
        this.clientConfigProvider = clientConfigProvider;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
            URI uri = requestContext.getUri();
            if (uri == null) {
                LogMessages.LOGGER.warn(Messages.MESSAGES.unableToLoadClientConfigProviderConfiguration());
                return;
            }
            String token = clientConfigProvider.getBearerToken(uri);
            if (token != null) {
                requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            } else {
                String username = clientConfigProvider.getUsername(uri);
                String password = clientConfigProvider.getPassword(uri);
                if (username != null && password != null) {
                    requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, BasicAuthHelper.createHeader(username, password));
                }
            }
        }
    }
}
