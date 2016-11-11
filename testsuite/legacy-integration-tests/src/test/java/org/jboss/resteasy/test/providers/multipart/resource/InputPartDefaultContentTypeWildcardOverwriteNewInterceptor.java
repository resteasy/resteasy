package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.test.providers.multipart.InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

@Provider
public class InputPartDefaultContentTypeWildcardOverwriteNewInterceptor implements
        javax.ws.rs.container.ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
                InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest.WILDCARD_WITH_CHARSET_UTF_8);
    }
}
