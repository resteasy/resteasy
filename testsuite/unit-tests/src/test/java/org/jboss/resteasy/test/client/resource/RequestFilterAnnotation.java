package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;

@Provider
public class RequestFilterAnnotation implements ClientRequestFilter {

    private static Logger logger = Logger.getLogger(RequestFilterAnnotation.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info("  ** ANnotation Filter");
        Annotation[] annotations = requestContext.getEntityAnnotations();
        Assert.assertNotNull("RequestContext doesn't contain annotations", annotations);
        requestContext.abortWith(Response.ok(annotations[0].annotationType().getName()).build());
    }
}
