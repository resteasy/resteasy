package org.jboss.resteasy.test.core.interceptors.resource;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.ArrayList;

@Priority(200)
public class ReaderContextSecondWriterInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = context.getHeaders();
        String header = (String) headers.getFirst(ReaderContextResource.HEADERNAME);
        if (header != null
                && header.equals(ReaderContextFirstWriterInterceptor.class.getName())) {
            context.setAnnotations(getClass().getAnnotations());
            context.setEntity(toList(getClass().getName()));
            context.setGenericType(String.class);
            context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
            context.setType(ArrayList.class);
        }
        context.proceed();
    }

    private static <T> ArrayList<T> toList(T o) {
        ArrayList<T> list = new ArrayList<T>();
        list.add(o);
        return list;
    }

}
