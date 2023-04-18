package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.test.providers.multipart.InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest;

@Provider
public class InputPartDefaultContentTypeWildcardOverwriteNewInterceptor implements
        jakarta.ws.rs.container.ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
                InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest.WILDCARD_WITH_CHARSET_UTF_8);
    }
}
