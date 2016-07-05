package org.jboss.resteasy.test.core.interceptors.resource;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

@Priority(200)
public class ReaderContextSecondReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        MultivaluedMap<String, String> headers = context.getHeaders();
        String header = headers.getFirst(ReaderContextResource.HEADERNAME);
        if (header != null
                && header.equals(ReaderContextFirstReaderInterceptor.class.getName())) {
            context.setAnnotations(getClass().getAnnotations());
            context.setInputStream(new ByteArrayInputStream(getClass()
                    .getName().getBytes()));
            context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
            context.setType(ArrayList.class);
        }
        return context.proceed();
    }
}
