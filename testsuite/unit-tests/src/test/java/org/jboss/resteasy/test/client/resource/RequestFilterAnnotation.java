package org.jboss.resteasy.test.client.resource;

import java.io.IOException;
import java.lang.annotation.Annotation;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@Provider
public class RequestFilterAnnotation implements ClientRequestFilter {

    private static Logger logger = Logger.getLogger(RequestFilterAnnotation.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info("  ** ANnotation Filter");
        Annotation[] annotations = requestContext.getEntityAnnotations();
        Assertions.assertNotNull(annotations, "RequestContext doesn't contain annotations");
        requestContext.abortWith(Response.ok(annotations[0].annotationType().getName()).build());
    }
}
