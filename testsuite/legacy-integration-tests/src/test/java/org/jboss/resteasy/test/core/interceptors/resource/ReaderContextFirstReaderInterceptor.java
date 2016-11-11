package org.jboss.resteasy.test.core.interceptors.resource;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

@Provider
@Priority(100)
public class ReaderContextFirstReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        MultivaluedMap<String, String> headers = context.getHeaders();
        String header = headers.getFirst(ReaderContextResource.HEADERNAME);
        if (header != null && header.equals(getClass().getName())) {
            context.setAnnotations(ReaderContextResource.class.getAnnotations());
            context.setInputStream(new ByteArrayInputStream(getClass()
                    .getName().getBytes()));
            context.setMediaType(MediaType.TEXT_HTML_TYPE);
            context.setType(LinkedList.class);
        }
        return context.proceed();
    }

}
