package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ReaderContextLinkedListEntityProvider implements
        MessageBodyReader<LinkedList<String>>,
        MessageBodyWriter<LinkedList<String>> {

    public static final String ERROR = "This LinkedList provider should never be used";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type == LinkedList.class;
    }

    @Override
    public long getSize(LinkedList<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return ERROR.length();
    }

    @Override
    public void writeTo(LinkedList<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        entityStream.write(ERROR.getBytes());
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return isWriteable(type, genericType, annotations, mediaType);
    }

    @Override
    public LinkedList<String> readFrom(Class<LinkedList<String>> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        LinkedList<String> list = new LinkedList<String>();
        list.add(ERROR);
        return list;
    }

}
