package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.io.InputStream;
import java.io.IOException;

@Provider
public class FormContainerRequestFilterFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        InputStream is = requestContext.getEntityStream();
        requestContext.setEntityStream(is);
    }
}