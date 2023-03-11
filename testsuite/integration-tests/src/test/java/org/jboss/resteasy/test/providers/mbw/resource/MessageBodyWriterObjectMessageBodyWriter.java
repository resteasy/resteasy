package org.jboss.resteasy.test.providers.mbw.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("xx/yy")
public class MessageBodyWriterObjectMessageBodyWriter implements MessageBodyWriter<Object> {

    public static volatile boolean used;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MessageBodyWriterObjectMessage.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        used = true;
        if (!(t instanceof MessageBodyWriterObjectMessage)) {
            throw new WebApplicationException();
        }
        MessageBodyWriterObjectMessage tm = (MessageBodyWriterObjectMessage) t;
        entityStream.write(tm.getS().getBytes(StandardCharsets.UTF_8));
    }
}
