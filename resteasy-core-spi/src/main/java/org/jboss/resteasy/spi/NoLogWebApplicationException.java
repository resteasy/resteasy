package org.jboss.resteasy.spi;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * WebApplicationExceptions are logged by RESTEasy. Use this exception when you don't want your exception logged
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NoLogWebApplicationException extends WebApplicationException {
    public NoLogWebApplicationException() {
    }

    public NoLogWebApplicationException(final Response response) {
        super(response);
    }

    public NoLogWebApplicationException(final int status) {
        super(status);
    }

    public NoLogWebApplicationException(final Response.Status status) {
        super(status);
    }

    public NoLogWebApplicationException(final Throwable cause) {
        super(cause);
    }

    public NoLogWebApplicationException(final Throwable cause, final Response response) {
        super(cause, response);
    }

    public NoLogWebApplicationException(final Throwable cause, final int status) {
        super(cause, status);
    }

    public NoLogWebApplicationException(final Throwable cause, final Response.Status status) {
        super(cause, status);
    }
}
