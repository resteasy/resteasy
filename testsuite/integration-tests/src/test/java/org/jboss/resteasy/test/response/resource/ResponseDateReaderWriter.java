package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Provider
public class ResponseDateReaderWriter implements MessageBodyReader<Date>,
        MessageBodyWriter<Date> {

    public static final int ANNOTATION_NONE = 0;
    public static final int ANNOTATION_CONSUMES = 1 << 2;
    public static final int ANNOTATION_PROVIDER = 1 << 3;
    public static final int ANNOTATION_UNKNOWN = 1 << 7;

    private final AtomicInteger atom;

    public ResponseDateReaderWriter(final AtomicInteger atom) {
        super();
        this.atom = atom;
    }

    @Override
    public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                        MediaType arg4) {
        return String.valueOf(Long.MAX_VALUE).length();
    }

    @Override
    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                               MediaType arg3) {
        return arg0 == Date.class;
    }

    @Override
    public void writeTo(Date date, Class<?> arg1, Type arg2, Annotation[] arg3,
                        MediaType arg4, MultivaluedMap<String, Object> arg5,
                        OutputStream stream) throws IOException, WebApplicationException {
        parseAnnotations(arg3);
        stream.write(String.valueOf(date.getTime()).getBytes());
    }

    @Override
    public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
                              MediaType arg3) {
        return isWriteable(arg0, arg1, arg2, arg3);
    }

    @Override
    public Date readFrom(Class<Date> arg0, Type arg1, Annotation[] arg2,
                         MediaType arg3, MultivaluedMap<String, String> arg4,
                         InputStream arg5) throws IOException, WebApplicationException {
        parseAnnotations(arg2);

        InputStreamReader reader = new InputStreamReader(arg5);
        BufferedReader br = new BufferedReader(reader);
        long date = Long.parseLong(br.readLine());
        return new Date(date);
    }

    protected void parseAnnotations(Annotation[] annotations) {
        int value = ANNOTATION_NONE;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Consumes.class) {
                    value |= ANNOTATION_CONSUMES;
                } else if (annotation.annotationType() == Provider.class) {
                    value |= ANNOTATION_PROVIDER;
                } else {
                    value |= ANNOTATION_UNKNOWN;
                }
            }
        }
        atom.set(value);
    }
}
