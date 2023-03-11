package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.logging.Logger;

public class RequestFilterThrowCustomException implements ClientRequestFilter {

    private static Logger logger = Logger.getLogger(RequestFilterThrowCustomException.class);

    @Override
    public void filter(ClientRequestContext requestContext) {
        logger.info("*** filter throwing exception ***");
        throw new ClientCustomException();
    }
}
