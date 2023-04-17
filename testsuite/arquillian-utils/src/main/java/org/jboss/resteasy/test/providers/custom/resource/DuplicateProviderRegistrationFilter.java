package org.jboss.resteasy.test.providers.custom.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.logging.Logger;

public class DuplicateProviderRegistrationFilter implements ClientRequestFilter {
    private static Logger logger = Logger.getLogger(DuplicateProviderRegistrationFilter.class);

    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        logger.info(DuplicateProviderRegistrationFilter.class.getName());
    }
}
