package org.jboss.resteasy.test.validation.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("application/foo")
@Consumes("application/foo")
public class ViolationExceptionReaderWriter
        implements MessageBodyReader<ViolationExceptionObject>, MessageBodyWriter<ViolationExceptionObject> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ViolationExceptionObject.class.equals(type);
    }

    public long getSize(ViolationExceptionObject t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    public void writeTo(ViolationExceptionObject t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        byte[] b = t.s.getBytes();
        entityStream.write(b.length);
        entityStream.write(t.s.getBytes());
        entityStream.flush();
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ViolationExceptionObject.class.equals(type);
    }

    public ViolationExceptionObject readFrom(Class<ViolationExceptionObject> type, Type genericType,
            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        int length = entityStream.read();
        byte[] b = new byte[length];
        entityStream.read(b);
        String s = new String(b);
        return new ViolationExceptionObject(s);
    }
}
