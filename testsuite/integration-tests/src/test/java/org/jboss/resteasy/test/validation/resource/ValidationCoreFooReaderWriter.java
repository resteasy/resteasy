package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/foo")
@Consumes("application/foo")
public class ValidationCoreFooReaderWriter implements MessageBodyReader<ValidationCoreFoo>, MessageBodyWriter<ValidationCoreFoo> {
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return ValidationCoreFoo.class.equals(type);
    }

    public long getSize(ValidationCoreFoo t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(ValidationCoreFoo t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        byte[] b = t.s.getBytes();
        entityStream.write(b.length);
        entityStream.write(t.s.getBytes());
        entityStream.flush();
    }

    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return ValidationCoreFoo.class.equals(type);
    }

    public ValidationCoreFoo readFrom(Class<ValidationCoreFoo> type, Type genericType,
                                      Annotation[] annotations, MediaType mediaType,
                                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        int length = entityStream.read();
        byte[] b = new byte[length];
        entityStream.read(b);
        String s = new String(b);
        return new ValidationCoreFoo(s);
    }
}

