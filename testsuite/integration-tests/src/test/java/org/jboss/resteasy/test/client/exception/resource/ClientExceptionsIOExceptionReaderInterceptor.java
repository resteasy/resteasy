package org.jboss.resteasy.test.client.exception.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

public class ClientExceptionsIOExceptionReaderInterceptor implements ReaderInterceptor {
    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        throw new IOException("client io");
    }
}
