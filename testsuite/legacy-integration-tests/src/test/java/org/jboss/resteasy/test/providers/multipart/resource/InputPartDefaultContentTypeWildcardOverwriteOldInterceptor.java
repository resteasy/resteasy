package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.test.providers.multipart.InputPartDefaultContentTypeWildcardOverwriteDeprecatedInterceptorTest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

@Provider
@ServerInterceptor
public class InputPartDefaultContentTypeWildcardOverwriteOldInterceptor implements
        PreProcessInterceptor {


    public ServerResponse preProcess(HttpRequest request,
                                     ResourceMethodInvoker method) throws Failure, WebApplicationException {
        request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
                InputPartDefaultContentTypeWildcardOverwriteDeprecatedInterceptorTest.WILDCARD_WITH_CHARSET_UTF_8);
        return null;
    }

}
