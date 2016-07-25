package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

public class ClientExceptionsIOExceptionReaderInterceptor implements ReaderInterceptor {
    @Override
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        throw new IOException("client io");
    }
}
