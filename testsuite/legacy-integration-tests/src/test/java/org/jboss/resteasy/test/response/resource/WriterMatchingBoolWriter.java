package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces(MediaType.WILDCARD)
public class WriterMatchingBoolWriter implements MessageBodyWriter<Object> {
    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(Boolean.class);
    }

    @Override
    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Boolean b = (Boolean) o;
        if (b.booleanValue()) {
            entityStream.write("YES".getBytes());
        } else {
            entityStream.write("NO".getBytes());
        }
    }
}
