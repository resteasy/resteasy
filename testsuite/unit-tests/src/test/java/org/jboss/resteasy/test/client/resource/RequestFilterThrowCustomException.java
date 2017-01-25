package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class RequestFilterThrowCustomException implements ClientRequestFilter {

    private static Logger logger = Logger.getLogger(RequestFilterThrowCustomException.class);

    @Override
    public void filter(ClientRequestContext requestContext) {
        logger.info("*** filter throwing exception ***");
        throw new ClientCustomException();
    }
}
