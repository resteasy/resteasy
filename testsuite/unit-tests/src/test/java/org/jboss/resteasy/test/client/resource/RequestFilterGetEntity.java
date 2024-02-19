package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

public class RequestFilterGetEntity implements ClientRequestFilter {

    private static Logger logger = Logger.getLogger(RequestFilterGetEntity.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info("*** filter 2 ***");
        Object entity = requestContext.getEntity();
        Assertions.assertEquals("test", entity, "The requestContext doesn't contain the correct entity");
        MediaType mt = requestContext.getMediaType();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, mt);
        Assertions.assertEquals(String.class, requestContext.getEntityType());
        requestContext.abortWith(Response.ok().build());

    }
}
