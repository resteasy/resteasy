package org.jboss.resteasy.test.client.resource;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

@Priority(200)
public class ClientResponseFilterInterceptorReaderTwo implements ReaderInterceptor {
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        throw new IOException("should be caught");
    }
}
