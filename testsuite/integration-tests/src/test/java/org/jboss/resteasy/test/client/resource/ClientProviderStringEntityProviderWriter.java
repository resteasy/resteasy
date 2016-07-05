package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ClientProviderStringEntityProviderWriter implements MessageBodyWriter<String> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == String.class;
    }

    @Override
    public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return s.length();
    }

    @Override
    public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(("Application defined provider writer: " + mediaType.toString() + httpHeaders.toString()).getBytes());
    }
}
