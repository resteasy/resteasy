package org.jboss.resteasy.core.interception.jaxrs;

import java.io.InputStream;
import java.net.URI;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseContainerRequestContext extends PreMatchContainerRequestContext {
    public ResponseContainerRequestContext(final HttpRequest request) {
        super(request, null, null);
    }

    @Override
    public void abortWith(Response response) {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }

    @Override
    public void setSecurityContext(SecurityContext context) {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }

    @Override
    public void setEntityStream(InputStream entityStream) {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }

    @Override
    public void setMethod(String method) {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }

    @Override
    public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }

    @Override
    public void setRequestUri(URI requestUri) throws IllegalStateException {
        throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
    }
}
