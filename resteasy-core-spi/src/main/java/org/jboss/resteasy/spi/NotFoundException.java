package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * Thrown by RESTEasy when HTTP Not Found issue (404) is encountered
 */
public class NotFoundException extends Failure
{

    public NotFoundException(final String s)
    {
        super(s, HttpResponseCodes.SC_NOT_FOUND);
    }

    public NotFoundException(final String s, final Response response)
    {
        super(s, response);
    }

    public NotFoundException(final String s, final Throwable throwable, final Response response)
    {
        super(s, throwable, response);
    }

    public NotFoundException(final String s, final Throwable throwable)
    {
        super(s, throwable, HttpResponseCodes.SC_NOT_FOUND);
    }

    public NotFoundException(final Throwable throwable)
    {
        super(throwable, HttpResponseCodes.SC_NOT_FOUND);
    }

    public NotFoundException(final Throwable throwable, final Response response)
    {
        super(throwable, response);
    }
}
